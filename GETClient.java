//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   GETClient.java                      //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

// **************************************************************************************
// Class Name: GETClient
// THe class will read the command line to find the server name and port number
// Then send a GET request for the ATOM feed
// And receive request message
// **************************************************************************************
// Initialize GETClient
public class GETClient {
    private int lamClock = 0; // initialize lamport clock
    private Writer writer;
    private Socket client;

    // Waiting to connect to the server and get the server name and port number
    public GETClient(String servername, int port) throws Exception {
        this.client = new Socket(servername, port);
        System.out.println("[CLIENT] Getting Client - Waiting for Server");
    }

    // Possible formats for the server name and port number include "http://servername.do.do:portnumber",
    // "http://servername:portnumber" (with implicit do information)
    //  and "servername:portnumber" (with implicit domain and protocol information).
    //
    // read hostname and port: 127.0.0.1 and 4567
    public static String[] stripURL(String address) throws Exception {
        URL url = new URL(address);
        int port = url.getPort();
        String hostname = url.getHost();
        String [] addr = new String[]{hostname,Integer.toString(port)};

        return addr;
    }


    //
    public void sendRequest(String outMessage) throws Exception {
        if (writer == null)
            writer = new OutputStreamWriter(this.client.getOutputStream());

        writer.write("[LamportClock] " + this.lamClock + "\n" + outMessage + "eof\n");
        writer.flush();

        this.lamClock++;

        System.out.println("[CLIENT] Sending Request - Successfully GET.");
        System.out.println("[CLIENT] Sending Request - Lamport CLock Updated -> " + this.lamClock);
    }

    // got message
    public void getRequest() {
        try{
            String lamClock = "";
            int num;
            Reader reader = new InputStreamReader(client.getInputStream());
            StringBuilder content = new StringBuilder();

            // 18s receive time
            client.setSoTimeout(18000);
            char[] input = new char[64];

            while ((num = reader.read(input)) != -1) {
                content.append(new String(input, 0, num));
            }

            // Select the large one & update lamport clock
            int begin = content.toString().indexOf("[LamportClock] ") + "[LamportClock] ".length();
            for(int i = begin; i < content.toString().length(); i++){
                if(content.toString().charAt(i) != '\n'){
                    lamClock += content.toString().charAt(i);
                }else
                    break;
            }
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            this.lamClock = Math.max(Integer.parseInt(lamClock), this.lamClock);
            this.lamClock++;
            System.out.println("[CLIENT] Getting Request - SUCCESSFULLY SENT MESSAGE");
            System.out.println("[CLIENT] Getting Request - Lamport CLock: " + this.lamClock);
            System.out.println("[TIMESTAMP]    " + formatter.format(date));
            System.out.println(content);

            reader.close();
            writer.close();
            client.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("FAIL to Get Requests");
        }
    }

    public static boolean ifSent = false;

    // Read in URL and client's name
    public static void startRequest(String URL, String name) throws Exception {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String[] strip = stripURL(URL);

                        GETClient client = new GETClient(strip[0], Integer.parseInt(strip[1]));

                        client.sendRequest("GET [CLIENT]: " + name);
                        client.getRequest();
                        ifSent = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("FAIL to start GET requests");
                    }
                }
            }).start();
    }
    // Start requesting
    public static void main(String[] input) throws Exception{
        try {
            startRequest(input[0],input[1]);
        } catch (IOException e) {
            System.out.println("FAIL to connect server");
        }
    }
}