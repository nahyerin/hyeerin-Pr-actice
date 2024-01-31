package multiChatFinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
 
public class MultiChatFinalServer {
    HashMap<String,HashMap<String,ServerRecThread>> groupMap; 
    ServerSocket serverSocket = null;
    Socket socket = null;
    static int connUserCount = 0; 
    
    // 비속어 필터링을 위한 금지어 목록
    private static final List<String> forbiddenWords = Arrays.asList("나쁜말1", "나쁜말2", "나쁜말3");

    // 비속어 필터링
    public boolean containsProfanity(String message) {
        for (String word : forbiddenWords) {
            // 대소문자 구분 없이 비속어가 포함되어 있는지 확인
            if (message.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
   
    //생성자
    public MultiChatFinalServer(){
    	groupMap = new HashMap<String,HashMap<String, ServerRecThread>>();
        Collections.synchronizedMap(groupMap);        
        HashMap<String,ServerRecThread> group01 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group01);        
        HashMap<String,ServerRecThread> group02 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group02);        
        HashMap<String,ServerRecThread> group03 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group03);        
        HashMap<String,ServerRecThread> group04 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group04); 
       
        groupMap.put("동방신기",group01);
        groupMap.put("원더걸스",group02);
        groupMap.put("샤이니",group03);
        groupMap.put("소녀시대",group04);
       
       
    }
   
    public void init(){
        try{
            serverSocket = new ServerSocket(7777);
            System.out.println("##서버가 시작되었습니다.");
           
            while(true){ 
                socket = serverSocket.accept(); 
                System.out.println(socket.getInetAddress()+":"+socket.getPort());
               
                Thread msr = new ServerRecThread(socket); 
                msr.start(); 
            }      
           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
   
    public void sendAllMsg(String msg){
       
        Iterator global_it = groupMap.keySet().iterator();
       
        while(global_it.hasNext()){
            try{
                HashMap<String, ServerRecThread> it_hash = groupMap.get(global_it.next());
                Iterator it = it_hash.keySet().iterator();
                while(it.hasNext()){
                    ServerRecThread st = it_hash.get(it.next());
                    st.out.writeUTF(msg);
                }              
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }
    }
   
    public void sendGroupMsg(String loc,String msg){      
       
        HashMap<String, ServerRecThread> gMap = groupMap.get(loc);    
        Iterator<String> group_it = groupMap.get(loc).keySet().iterator();        
        while(group_it.hasNext()){
            try{    
                    ServerRecThread st = gMap.get(group_it.next());
                    if(!st.chatMode){ 
                        st.out.writeUTF(msg);  
                    }
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }  
    }
   
   
    public void sendPvPMsg(String loc,String fromName, String toName, String msg){
     
            try {
            	groupMap.get(loc).get(toName).out.writeUTF(msg);
            	groupMap.get(loc).get(fromName).out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
         
    }
   
    public void sendToMsg(String loc, String fromName, String toName, String msg){     
         
        try{   
           
           
        	groupMap.get(loc).get(toName).out.writeUTF("whisper|"+fromName+"|"+msg);
        	groupMap.get(loc).get(fromName).out.writeUTF("whisper|"+fromName+"|"+msg);
               
           }catch(Exception e){
                System.out.println("예외:"+e);
           }
     }
    
    public String getEachMapSize(){
        return getEachMapSize(null);    
    }
    public String getEachMapSize(String loc){
       
        Iterator global_it = groupMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        int sum=0;
        sb.append("=== 채팅방 그룹 목록 ==="+System.getProperty("line.separator"));
        while(global_it.hasNext()){
            try{
                String key = (String) global_it.next();
               
                HashMap<String, ServerRecThread> it_hash = groupMap.get(key);
                int size = it_hash.size();
                sum +=size;
                sb.append(key+": ("+size+"명)"+(key.equals(loc)?"(*)":"")+"\r\n");
               
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }
        sb.append("동시 접속 인원 :"+ sum+ "명 \r\n");
        return sb.toString();
    }
        
public boolean isNameGlobla(String name){
        boolean result=false;
        Iterator<String> global_it = groupMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                String key = global_it.next();             
                HashMap<String, ServerRecThread> it_hash = groupMap.get(key);
                if(it_hash.containsKey(name)){
                    result= true;
                    break;
                }
               
            }catch(Exception e){
                System.out.println("isNameGlobla()예외:"+e);
            }
        }
     
        return result;
    }
    public String nVL(String str, String replace){
        String output="";
        if(str==null || str.trim().equals("")){
            output = replace;      
        }else{
            output = str;
        }
        return output;     
    }
   
    public static void main(String[] args) {
        MultiChatFinalServer ms = new MultiChatFinalServer(); 
        ms.init();
    } 
  
    class ServerRecThread extends Thread {
       
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name="";
        String loc="";
        String toNameTmp = null;
        String fileServerIP;
        String filePath;
        boolean chatMode;
       
        public ServerRecThread(Socket socket){
            this.socket = socket;
            try{
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                System.out.println("ServerRecThread 생성자 예외:"+e);
            }
        }
             
        public String showUserList(){
           
            StringBuilder output = new StringBuilder("== 동시 접속자 목록 ==\r\n");
            Iterator it = groupMap.get(loc).keySet().iterator(); 
            while(it.hasNext()){ 
                 try{
                    String key= (String) it.next();                                    
                    
                    if(key.equals(name)){ 
                        key += " (*) ";
                    }                     
                    output.append(key+"\r\n");                  
                 }catch(Exception e){
                     System.out.println("예외:"+e);
                 }
             }
            output.append("=="+ groupMap.get(loc).size()+"명 접속중==\r\n");
            System.out.println(output.toString());
            return output.toString();
         }
        
       public String[] getMsgParse(String msg){
            System.out.println("msgParse():msg?   "+ msg);         
            String[] tmpArr = msg.split("[|]");        
            return tmpArr;
        }
       
       
        @Override
        public void run(){ 
            HashMap<String, ServerRecThread> clientMap=null;       
           
            try{  
                while(in!=null){ 
                    String msg = in.readUTF();                  
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    
                 // 비속어 필터링
                    if (containsProfanity(msgArr[1])) {
                        out.writeUTF("show|[##] 비속어가 포함된 메시지는 전송이 제한됩니다.");
                        continue; 
                    }
                   
                    if(msg.startsWith("req_logon")){                  
                      
                        if(!(msgArr[0].trim().equals(""))&&!isNameGlobla(msgArr[0])){                      
                            name = msgArr[0];
                            MultiChatFinalServer.connUserCount++; 
                            out.writeUTF("logon#yes|"+getEachMapSize()); 
                        }else{
                             out.writeUTF("logon#no|err01");
                        }                       
                    }else if(msg.startsWith("req_enterRoom")){ 
                         loc = msgArr[1];
                         
                         if(isNameGlobla(msgArr[0])){
                             out.writeUTF("logon#no|"+name);  
                             
                         }else if(groupMap.containsKey(loc)){
                             sendGroupMsg(loc, "show|[##] "+name + "님이 입장하셨습니다.");
                             clientMap= groupMap.get(loc); 
                             clientMap.put(name, this); 
                             System.out.println(getEachMapSize());                   
                             out.writeUTF("enterRoom#yes|"+loc); 
                             
                         }else{                        
                             out.writeUTF("enterRoom#no|"+loc);                              
                         }
                       
                         
                    }else if(msg.startsWith("req_cmdMsg")){ 
                        if(msgArr[1].trim().equals("/접속자")){
                            out.writeUTF("show|"+showUserList()); 
                       
                           
                        }else if(msgArr[1].trim().startsWith("/귓속말")){
                            String[] msgSubArr = msgArr[1].split(" ",3); 
                           
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] 귓속말 사용법이 잘못되었습니다."
                                		+ "\r\n usage : /귓속말 [상대방이름] [보낼메시지].");
                            }else if(name.equals(msgSubArr[1])){
                                out.writeUTF("show|[##] 자신에게 귓속말을 할 수 없습니다."
                                		+ "\r\n usage : /귓속말 [상대방이름] [보낼메시지].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ 
                                    System.out.println("귓속말!");
                                    sendToMsg(loc,name,toName,toMsg);
                                   
                                }else{
                                    out.writeUTF("show|[##] 해당 유저가 존재하지 않습니다.");
                                }
                                    }
                           
                           
                        }else if(msgArr[1].trim().startsWith("/채팅방")){                          
                           
                            String[] msgSubArr = msg.split(" ");                           
                            if(msgSubArr.length==1){                              
                                out.writeUTF("show|"+getEachMapSize(loc));                             
                            }else if(msgSubArr.length==2) {
                                String tmpLoc = msgSubArr[1]; 
                               
                                if(loc.equals(tmpLoc)){
                                    out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다."
                                    		+ "\r\n 본인이 참여하고 있는 채팅방을 지정할 수 없습니다.\r\n " +
                                                "usage : 채팅방목록 보기 : /채팅방" +
                                                "\r\n usage : 채팅방변경 하기 : /채팅방 [변경할방이름].");
                                    continue;
                                }
                               
                                if(groupMap.containsKey(tmpLoc)&& !this.chatMode){ 
                                        out.writeUTF("show|[##] 채팅방을 "+loc+"에서 "+ tmpLoc+"로 변경합니다. ");                        
                                   
                                        clientMap.remove(name); 
                                        sendGroupMsg(loc, "show|[##] "+name+"님이 퇴장하셨습니다.");
                                       
                                        System.out.println("이전채팅방("+loc+")에서 에서 "+name +"제거");
                                        loc = tmpLoc;
                                        clientMap = groupMap.get(loc);
                                        sendGroupMsg(loc, "show|[##] "+name+"님이 입장하셨습니다.");
                                        clientMap.put(name, this); 
                               
                                }else{
                                    out.writeUTF("##입력한 채팅방이 존재하지 않거나 현재 이동 할 수 없는 상태입니다.");
                                }
                               
                            }else{
                                out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다.\r\n " +
                                        "usage : 채팅방 목록 보기 : /채팅방" +
                                        "\r\n usage : 채팅방변경 하기 : /채팅방 [변경할방이름].");
                               
                            }
                           
                           
                        }else if(msgArr[1].trim().startsWith("/대화신청")){
                            String[] msgSubArr =  msgArr[1].split(" ",2);
                                               
                           
                            if(msgSubArr.length!=2){
                                out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다.\r\n " +
                                        "usage : 1:1 대화신청하기 : /대화신청 [상대방대화명]");
                                continue;
                            }else if(name.equals(msgSubArr[1])){
                                    out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다."
                                    		+ "\r\n 본인의 대화명을 지정하실수없습니다.1:1대화를 할 상대방의 "
                                    		+ "대화명을 지정해주세요.\r\n " +
                                            "usage : 1:1 대화신청하기 : /대화신청 [상대방대화명]");
                                continue;
                            }
                           
                            if(!chatMode){
                               
                                String toName = msgSubArr[1].trim();
                                out.writeUTF("show|[##] "+toName +"님께 대화신청을 합니다. ");
                                if(clientMap.containsKey(toName) && !clientMap.get(toName).chatMode){ 
                                    clientMap.get(toName).out.writeUTF("req_PvPchat|[##] "+name+"님께서 "
                                    		+ "1:1대화신청을 요청하였습니다\r\n 수락하시겠습니까?(y,n)");  
                                    toNameTmp = toName;
                                    clientMap.get(toNameTmp).toNameTmp = name;
                                }else{
                                    out.writeUTF("show|[##] 해당 유저가 존재하지않거나 상대방이 1:1대화를 할수없는 상태입니다.");
                                }
                               
                            }else{
                                out.writeUTF("show|[##] 1:1대화 모드이므로 대화신청을 하실수없습니다.");
                            }
                           
                        }else if(msgArr[1].startsWith("/대화종료")){
                             
                            if(chatMode){
                                chatMode = false;
                                out.writeUTF("show|[##] "+toNameTmp+"님과 1:1대화를 종료합니다.");
                                clientMap.get(toNameTmp).chatMode=false;
                                clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name +"님께서 1:1대화를 종료하였습니다");
                                toNameTmp="";
                                clientMap.get(toNameTmp).toNameTmp="";
                               
                            }else{
                                out.writeUTF("show|[##] 1:1대화중일때만 사용할수있는 명령어입니다. ");
                            }
                           
                        }else if(msgArr[1].trim().startsWith("/파일전송")){  
                           
                            if(!chatMode){
                                out.writeUTF("show|[##] 1:1대화중일때만 사용할수있는 명령어입니다. ");
                                continue;                              
                            }
                           
                            String[] msgSubArr = msgArr[1].split(" ",2);
                            if(msgSubArr.length!=2){
                                out.writeUTF("show|[##] 파일전송 명령어 사용법이 잘못되었습니다."
                                		+ "\r\n usage : /파일전송 [전송할파일경로]");
                                continue;                              
                            }
                            filePath = msgSubArr[1];                           
                            File sendFile = new File(filePath);
                            String availExtList = "txt,java,jpeg,jpg,png,gif,bmp";
                           
                           
                            if(sendFile.isFile()){                     
                                String fileExt = filePath.substring(filePath.lastIndexOf(".")+1);
                                if(availExtList.contains(fileExt)){
                                    Socket s = groupMap.get(loc).get(toNameTmp).socket;
                                    System.out.println("s.getInetAddress():파일서버아이피=>"+s.getInetAddress());
                                  
                                    fileServerIP = s.getInetAddress().getHostAddress();
                                    clientMap.get(toNameTmp).out.writeUTF("req_fileSend|[##] "
                                    +name +"님께서 파일["+sendFile.getName()+"] 전송을 시도합니다. "
                                    		+ "\r\n수락하시겠습니까?(Y/N)");                        
                                    out.writeUTF("show|[##] "+toNameTmp +"님께 파일["+
                                    		sendFile.getAbsolutePath()+"] 전송을 시도합니다.");
                                   
                                }else{
                                   
                                    out.writeUTF("show|[##] 전송가능한 파일이 아닙니다. \r\n["
                                    +availExtList+"] 확장자를 가진 파일만 전송가능합니다.");                             
                                }       
                           
                            }else{                             
                                out.writeUTF("show|[##] 존재하지 않는 파일입니다.");                            
                            } 
                        }else{
                            out.writeUTF("show|[##] 잘못된 명령어입니다.");
                        }
                       
                    }else if(msg.startsWith("req_say")){ 
                          if(!chatMode){
                            sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                          }else{
                            sendPvPMsg(loc, name,toNameTmp , "say|"+name+"|"+msgArr[1]);
                          }
                    }else if(msg.startsWith("req_whisper")){ 
                        if(msgArr[1].trim().startsWith("/귓속말")){
                            String[] msgSubArr = msgArr[1].split(" ",3);
                                                           
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] 귓속말 사용법이 잘못되었습니다."
                                		+ "\r\n usage : /귓속말 [상대방이름] [보낼메시지].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ 
                                    sendToMsg(loc,name,toName,toMsg);
                                   
                                }else{
                                    out.writeUTF("show|[##] 해당 유저가 존재하지 않습니다.");
                                }
                            }
                        }
                    }else if(msg.startsWith("PvPchat")){    
                        String result = msgArr[0];                             
                        if(result.equals("yes")){                              
                            chatMode = true;    
                            clientMap.get(toNameTmp).chatMode=true;
                            System.out.println("##1:1대화 모드 변경");                               
                            try {
                                out.writeUTF("show|[##] "+toNameTmp + "님과 1:1 대화를 시작합니다.");
                                clientMap.get(toNameTmp).out.writeUTF("show|[##] "
                                +name + "님과 1:1 대화를 시작합니다.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "
                        +name+" 님께서 대화신청을 거절하셨습니다.");
                        }                      
                       
                    } else if(msg.startsWith("fileSend")){
                        String result = msgArr[0];
                        if(result.equals("yes")){
                            System.out.println("##파일전송##YES");                             
                            try {                      
                                String tmpfileServerIP = clientMap.get(toNameTmp).fileServerIP;
                                String tmpfilePath = clientMap.get(toNameTmp).filePath;
                               
                                clientMap.get(toNameTmp).out.writeUTF("fileSender|"+tmpfilePath);
                            
                                String fileName = new File(tmpfilePath).getName();
                                out.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);                                        
                               
                                clientMap.get(toNameTmp).filePath="";
                                clientMap.get(toNameTmp).fileServerIP="";
                               
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "
                        +name+" 님께서 파일전송을 거절하였습니다.");
                        }                  
                       
                    }else if(msg.startsWith("req_exit")){ 
                       
                    }
                
                }
            }catch(Exception e){
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
              
            }finally{
              
                if(clientMap!=null){
                    clientMap.remove(name);
                    sendGroupMsg(loc,"## "+ name + "님이 퇴장하셨습니다.");
                    System.out.println("##현재 동시 접속자는 "
                    +(--MultiChatFinalServer.connUserCount)+"명 입니다.");
                }              
            }
        }
    }
}