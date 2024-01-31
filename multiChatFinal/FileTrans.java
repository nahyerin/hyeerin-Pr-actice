package multiChatFinal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileTrans {
    // 파일 전송
    static class FileSender extends Thread {
        private String filePath;

        public FileSender(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(7778);		// 포트번호 서버랑 충돌해서 다르게 두기!
                serverSocket.setReuseAddress(true);
                System.out.println("====================> 파일서버 세팅완료");
                serverSocket.setSoTimeout(0);

                Socket socket = serverSocket.accept();
                System.out.println("====================> 파일전송 시작");

                try (
                        FileInputStream fis = new FileInputStream(filePath);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        OutputStream out = socket.getOutputStream();
                        BufferedOutputStream bos = new BufferedOutputStream(out)
                ) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("====================> 파일전송 완료");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("====================> 파일전송 실패");
            }
        }
    }

    // 파일 수신 
    static class FileReceiver extends Thread {
        private Socket socket;
        private InputStream in;
        private FileOutputStream fos;
        private String filename;
        private BufferedInputStream bis;
        private BufferedOutputStream bos;

        public FileReceiver(String ip, String filename) {
            try {
                this.filename = filename;
                socket = new Socket("172.20.10.4", 7778); // 서버의 IP 주소와 포트로 소켓 생성 (FileSender와는 같아야 함)
                System.out.println("====================> 파일다운로드 시작");
                in = socket.getInputStream();
                bis = new BufferedInputStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String fileSeparator = System.getProperty("file.separator");
                File f = new File("Down");
                if (!f.isDirectory()) {
                    f.mkdir();
                }

                String originalFilePath = "Down" + fileSeparator + filename;
                String filePath = generateUniqueFileName(originalFilePath);

                fos = new FileOutputStream(filePath);
                bos = new BufferedOutputStream(fos);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                System.out.println("====================> " + filePath);
                System.out.println("====================> 파일다운로드 완료");

            } catch (FileNotFoundException e) {
                System.out.println("예외: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("====================> 파일전송 실패");
            } finally {
                try {
                    bos.close();  
                    fos.close();  
                    bis.close();
                    in.close();   
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 파일중복시 '언더바+숫자'로 이름설정해서 정상적으로 별도저장되게 하기
        private String generateUniqueFileName(String filePath) {
            File file = new File(filePath);
            if (file.exists()) {
                String baseName = filePath.substring(0, filePath.lastIndexOf('.'));
                String extension = filePath.substring(filePath.lastIndexOf('.'));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = dateFormat.format(new Date());
                int counter = 1;

                String newFilePath;
                do {
                    newFilePath = baseName + "_" + counter + extension;
                    counter++;
                } while (new File(newFilePath).exists());

                return newFilePath;
            } else {
                return filePath;
            }
        }
    }
}
