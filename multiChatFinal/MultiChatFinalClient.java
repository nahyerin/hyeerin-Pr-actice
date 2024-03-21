package multiChatFinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import multiChatFinal.FileTrans.FileReceiver;
import multiChatFinal.FileTrans.FileSender;

public class MultiChatFinalClient {

    static boolean chatmode = false; // 채팅 모드 여부 
    static int chatState = 0; // 채팅 상태 확인

    public static void main(String[] args) throws IOException {

        try {
            String ServerIP = "172.20.10.4"; // 서버 IP 주소
            Socket socket = new Socket(ServerIP, 7777); // 서버에 연결하는 소켓 생성 , 파일전송은 따로 두자!
            System.out.println("[##] 서버와 연결되었습니다......");

            Thread sender = new Sender(socket);
            Thread receiver = new Receiver(socket);

            sender.start();
            receiver.start();

        } catch (Exception e) {
            System.out.println("예외[MultiClient class]:" + e);
        }
    }
}

// Receiver
class Receiver extends Thread {

    Socket socket;
    DataInputStream in;

    public Receiver(Socket socket) {
        this.socket = socket;

        try {
            in = new DataInputStream(this.socket.getInputStream());
        } catch (Exception e) {
            System.out.println("예외:" + e);
        }
    }

    // 메시지 파싱
    public String[] getMsgParse(String msg) {
        String[] tmpArr = msg.split("[|]");
        return tmpArr;
    }

    @Override
    public void run() {
        while (in != null) {
            try {
                String msg = in.readUTF();
                String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|") + 1));

                // 서버로부터 이용자 명령어 받아서 처리하기
                if (msg.startsWith("logon#yes")) {
                    MultiChatFinalClient.chatState = 1;
                    System.out.println(msgArr[0]);
                    System.out.println("▶ 입장하실 채팅방 이름을 입력해 주세요:");

                } else if (msg.startsWith("logon#no")) {
                    MultiChatFinalClient.chatState = 0;
                    System.out.println("[##] 중복된 대화명이 존재합니다\n▶ 대화명을 다시 입력해 주세요:");

                } else if (msg.startsWith("enterRoom#yes")) {
                    System.out.println("[##] 채팅방 (" + msgArr[0] + ") 에 입장하였습니다.");
                    MultiChatFinalClient.chatState = 2;

                } else if (msg.startsWith("enterRoom#no")) {
                    System.out.println("[##] 입력하신 [" + msgArr[0] + "]는 존재하지 않는 채팅방입니다.");
                    System.out.println("▶ 채팅방 이름을 다시 입력해 주세요:");

                } else if (msg.startsWith("show")) {
                    System.out.println(msgArr[0]);

                } else if (msg.startsWith("say")) {
                    System.out.println("[" + msgArr[0] + "] " + msgArr[1]);

                } else if (msg.startsWith("whisper")) {
                    System.out.println("[귓][" + msgArr[0] + "] " + msgArr[1]);

                } else if (msg.startsWith("req_PvPchat")) {
                    MultiChatFinalClient.chatState = 3;
                    System.out.println(msgArr[0]);
                    System.out.print("▶ 선택:");

                } else if (msg.startsWith("req_fileSend")) {
                    MultiChatFinalClient.chatState = 5;
                    System.out.println(msgArr[0]);
                    System.out.print("▶ 선택:");
                    sleep(100);

                } else if (msg.startsWith("fileSender")) {
                    System.out.println("fileSender:" + InetAddress.getLocalHost().getHostAddress());
                    System.out.println("fileSender:" + msgArr[0]);
                    try {
                        new FileSender(msgArr[0]).start();
                    } catch (Exception e) {
                        System.out.println("FileSender 오류:");
                        e.printStackTrace();
                    }

                } else if (msg.startsWith("fileReceiver")) {
                    System.out.println("fileReceiver:" + InetAddress.getLocalHost().getHostAddress());
                    System.out.println("fileReceiver:" + msgArr[0] + "/" + msgArr[1]);
                    String ip = msgArr[0];
                    String fileName = msgArr[1];
                    try {
                        new FileReceiver(ip, fileName).start();
                    } catch (Exception e) {
                        System.out.println("FileSender 오류:");
                        e.printStackTrace();
                    }

                } else if (msg.startsWith("req_exit")) {
                
                }
            } catch (SocketException e) {
                System.out.println("예외:" + e);
                System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
                return;
            } catch (Exception e) {
                System.out.println("Receiver:run() 예외:" + e);
            }
        }
    }
}

// Sender 
class Sender extends Thread {

    Socket socket;
    DataOutputStream out;
    String name;

    public Sender(Socket socket) {
        this.socket = socket;
        try {
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("예외:" + e);
        }
    }

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        System.out.println("▶대화명을 입력해 주세요:");
        while (out != null) {
            try {
                String msg = s.nextLine();
                if (msg == null || msg.trim().equals("")) {
                    msg = " ";
                }

                // 채팅 상태에 따라서 메시지 보내기!
                if (MultiChatFinalClient.chatState == 0) {
                    if (!msg.trim().equals("")) {
                        name = msg;
                        out.writeUTF("req_logon|" + msg);
                    } else {
                        System.out.println("[##] 대화명으로 공백을 입력할 수 없습니다.\r\n" +
                                "▶ 대화명을 다시 입력해 주세요:");
                    }
                } else if (MultiChatFinalClient.chatState == 1) {
                    if (!msg.trim().equals("")) {
                        out.writeUTF("req_enterRoom|" + name + "|" + msg);
                    } else {
                        System.out.println("[##] 공백을 입력할 수 없습니다.\r\n" +
                                "▶ 채팅방 이름을 다시 입력해 주세요:");
                    }
                } else if (msg.trim().startsWith("/")) {
                    if (msg.equalsIgnoreCase("/exit")) {
                        System.out.println("[##] 클라이언트를 종료합니다.");
                        System.exit(0);
                        break;
                    } else {
                        out.writeUTF("req_cmdMsg|" + name + "|" + msg);
                    }
                } else if (MultiChatFinalClient.chatState == 3) {
                    msg = msg.trim();
                    if (msg.equalsIgnoreCase("y")) {
                        out.writeUTF("PvPchat|yes");
                    } else if (msg.equalsIgnoreCase("n")) {
                        out.writeUTF("PvPchat|no");
                    } else {
                        System.out.println("-입력값 오류-");
                        out.writeUTF("PvPchat|no");
                    }
                    MultiChatFinalClient.chatState = 2;
                } else if (MultiChatFinalClient.chatState == 5) {
                    if (msg.trim().equalsIgnoreCase("y")) {
                        out.writeUTF("fileSend|yes");
                    } else if (msg.trim().equalsIgnoreCase("n")) {
                        out.writeUTF("fileSend|no");
                    } else {
                        System.out.println("-입력값 오류-");
                        out.writeUTF("fileSend|no");
                    }
                    MultiChatFinalClient.chatState = 2;
                } else {
                    out.writeUTF("req_say|" + name + "|" + msg);
                }
            } catch (SocketException e) {
                System.out.println("Sender:run()예외:" + e);
                System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
                return;
            } catch (IOException e) {
                System.out.println("예외:" + e);
            }
        }
    }
}
