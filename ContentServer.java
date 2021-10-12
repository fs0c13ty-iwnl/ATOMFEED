//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   ContentServer.java                  //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

// **************************************************************************************
// Class Name: Content Server
// Reading two parameters from the command line,
// where the first is the server name and port number (as for GET)
// and the second is the location of a file in the file system local to the Content Serve
// **************************************************************************************
// Initialize Content Server
public class ContentServer {
    private int lamClock = 0; // initialize lamport clock
    private Writer writer;
    private String client;
    private Socket server;

    // Waiting to connect to the server and get the server name and port number
    public ContentServer(String serverName, int port, String clientName) throws Exception {
        this.server = new Socket(serverName, port);
        this.client = clientName;
        System.out.println("[SERVER] ContentServer - Waiting for Client to Connect");
    }


    // By writing message and then send
    public void sendRequest(String outMessage) throws Exception {
        if (writer == null)
            writer = new OutputStreamWriter(this.server.getOutputStream());

        writer.write("[LamportClock] " + this.lamClock + "\n" + outMessage + "eof\n");
        writer.flush();

        this.lamClock++;

        System.out.println("[SERVER] Sending Request - Successfully PUT");
        System.out.println("[SERVER] Sending Request - Lamport CLock Updated -> " + this.lamClock);
    }

    // Get message
    public void getRequest() {
        try{
            String lamClock = "";
            int num;
            Reader reader = new InputStreamReader(server.getInputStream());
            StringBuilder content = new StringBuilder();

            // 18s receive time
            server.setSoTimeout(18000);
            char[] chars = new char[64];

            while ((num = reader.read(chars)) != -1) {
                content.append(new String(chars, 0, num));
            }

            // Select the bigger value
            int begin = content.indexOf("[LamportClock] ") + "[LamportClock] ".length();

            for(int i = begin; i < content.length(); i++){
                if(content.charAt(i)!='\n'){
                    lamClock += content.toString().charAt(i);
                } else
                    break;
            }
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());

            this.lamClock = Math.max(Integer.parseInt(lamClock), this.lamClock);
            this.lamClock++;
            System.out.println("[SERVER] Getting Request - SUCCESSFULLY SENT MESSAGE");
            System.out.println("[SERVER] Getting Request - Lamport Clock: " + this.lamClock);
            System.out.println("[TIMESTAMP]    " + formatter.format(date));
            System.out.println(content);

            reader.close();
            writer.close();
            server.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[SERVER] FAIL to Get Requests");
        }
    }

    public static boolean ifSent = false;

    // By using thread to run and process the message
    public static void startRequest(String URL, String name) throws Exception{
        new Thread(() -> {
            try {
                String[] strip = GETClient.stripURL(URL);
                ContentServer server = new ContentServer(strip[0], Integer.parseInt(strip[1]), name);
                ATOMFeed atomFeed = new ATOMFeed(server.client);

                // PUT message should take the format
                String update = "PUT /atom.xml HTTP/1.1\n" + "User-Agent: " + server.client + "\n";
                String content = "Content-Type: [application/atom.xml]\n" + "Content-Length: " + atomFeed.Length + "\n\n";

                server.sendRequest(update + content + atomFeed.content);
                server.getRequest();

                ifSent = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[SERVER] FAIL to start GET requests");
            }
        }).start();
    }
    // Start requesting
    public static void main(String[] input) throws Exception{
        try {
            startRequest(input[0],input[1]);
        } catch (IOException e) {
            System.out.println("[CLIENT] FAIL to connect server");
        }
    }

}