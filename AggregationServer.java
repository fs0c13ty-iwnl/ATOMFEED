//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   AggregationServer.java              //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//
import java.io.*;
import java.net.Socket;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

// **************************************************************************************
// Class Name: AggregationServer
// The Aggregation server can open a socket server
// The Aggregation server will initiate and schedule Producer and Consumer
// **************************************************************************************
public class AggregationServer {
    public static int lamClock = 0; // Set default lamport clock
    public static boolean response = true;

    public static void main (String[] args) {
        // start on port 4567
        int port = 4567;
        if(args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        // get the server clock from lamport
        // When start the clock, it will be start at 0
        int clock = 0;
        process(port, clock);
    }
    // Initialise the two thread and use the blocking queue
    public static void process(int port, int lamClock){
        BlockingQueue<Socket> queue = new LinkedBlockingQueue<Socket>();

        Consumer consumer = new Consumer(queue, lamClock);
        Producer producer = new Producer(queue, port, lamClock);

        // Start the run function in each thread
        new Thread(producer, "[ATOM] Producer").start();
        new Thread(consumer, "[ATOM] Consumer").start();
    }
}
// **************************************************************************************
// Class Name: Producer
// A producer class will listen for requests
// If there is a new request, push it into the consumer queue
// **************************************************************************************
class Producer implements Runnable {
    final BlockingQueue<Socket> proQueue;
    int port;
    int lamClock;

    public Producer(BlockingQueue<Socket> queue, int port, int lamCLock) {
        this.port = port;
        this.lamClock = lamCLock;
        this.proQueue = queue;
    }

    public void run(){
        synchronized (proQueue) {
            try {
                int port = this.port;
                int maxTaskNum = 50;
                ServerSocket server = new ServerSocket(port);
                System.out.println("[Atom Server has Initiated!]");
                System.out.println("[Waiting for Requests]");
                System.out.println("[IMPORTANT] Press CTRL+C to terminate server");
                System.out.println("[IMPORTANT] Type $ kill -9 `lsof -t -i:4567` to release the port\n");
                while (true) {
                    if (proQueue.size() < maxTaskNum) {
                        // The server has been accept
                        Socket socket = server.accept();
                        System.out.println("[ATOM] Producer - Request-Type [GET]");
                        proQueue.put(socket);

                        // update clock
                        AggregationServer.lamClock++;
                        System.out.println("[ATOM] Producer - Lamport Clock: " + AggregationServer.lamClock);
                    } else {
                        System.out.println("[ALERT] Producer Queue FUlL, FAIL TO ACCEPT CLIENT");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
// **************************************************************************************
// Class Name: Consumer
// The Consumer will wait for Requests from Producer
// When received a new request, The Consumer will take it and deal with the task
// **************************************************************************************
class Consumer implements Runnable{
    int lamClock;
    BlockingQueue<Socket> conQueue;

    // Initialize Consumer
    public Consumer(BlockingQueue<Socket> queue, int lamClock){
        this.lamClock = lamClock;
        this.conQueue = queue;
    }

    // The class will deal with any tasks popped from queue
    static class consumerList {
        int clock;
        private final Socket socket;

        public consumerList(Socket socket, int clock) {
            this.socket = socket;
            this.clock = clock;
        }

        // Respond to PUT request
        public static String respondToPUT(ArrayList<String> buildList) {
            ArrayList<String> XML = new ArrayList<>();
            for(int i = 6; i < buildList.size(); i++)
                XML.add(buildList.get(i));

            // Convert form
            // Check is valid
            XMLParser parse = new XMLParser();
            boolean validated = parse.XML_linting(XML);

            //if the xml is invalid, return 500 bad request
            if(!validated) {
                // 500 INTERNAL_ERROR
                String INTERNAL_SERVER_ERROR = " INTERNAL SERVER ERROR\n" + "[User-Agent]   Aggregation Server\n";
                return "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_INTERNAL_ERROR + INTERNAL_SERVER_ERROR;
            }

            // Name of the content server
            String CSName = buildList.get(2).substring(12);
            String pathName = System.getProperty("java.class.path") + "/contents/";
            boolean ifExists = feed(parse.text,pathName + CSName+".txt");

            if(ifExists)
                // 200 OK
                return "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_OK + " OK\n" + "[User-Agent]   Aggregation Server\n";
            // 204 NO CONTENT then 201 CREATED
            return "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_NO_CONTENT + " NO CONTENT\n" + "[User-Agent]   Aggregation Server\n" +
                    "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_CREATED + " CREATED\n" + "[User-Agent]   Aggregation Server\n";
        }

        // Respond to GET requests
        // Delete contents if no updates in 12 secs
        public static String respondToGET() {
            // 200 OK
            String upload = "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_OK + " OK\n" +"[User-Agent]   Aggregation Server\n";;
            String filepath = System.getProperty("java.class.path") + "/contents";
            StringBuilder content = new StringBuilder();
            try {
                content.append("<?xml version='1.0' encoding='iso-8859-1' ?>\n");
                content.append("<feed xml:lang='en-US' xmlns=\"http://www.w3.org/2005/Atom\">\n");
                File PATH = new File(filepath);

                if(PATH.isDirectory()){
                    File[] file = PATH.listFiles();

                    // Search for all feeds files
                    for(int i = 0; i < Objects.requireNonNull(file).length; i++){
                        FileReader reader = new FileReader(filepath+"/"+file[i].getName());
                        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                            ArrayList<String> text = new ArrayList<>();
                            boolean expireFlag = false;
                            String temp;
                            int flag = 0;
                            while ((temp = bufferedReader.readLine()) != null) {
                                // Wait the 12s, delete it if it expires,
                                if (flag == 0 && ifExpire(temp, 12000)) {
                                    reader.close();
                                    bufferedReader.close();
                                    file[i].delete();
                                    expireFlag = true;
                                    break;
                                }
                                text.add(temp);
                                flag++;
                            }
                            // from txt to XML
                            if (!expireFlag) {
                                XMLParser Aggregation = new XMLParser();
                                content.append(Aggregation.parse(text));
                                reader.close();
                            }
                        }
                    }
                    // Update Lamport
                    AggregationServer.lamClock++;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Fail to get requests from producer");
            }
            content.append("</feed>");
            String output = upload + content;

            return output;
        }

        // Pop out from queue
        public void pop() {
            try{
                InputStreamReader ISR = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(ISR);
                StringBuilder string = new StringBuilder();
                ArrayList<String> builds = new ArrayList<>();

                String line;
                String sendStatus = null;

                while ((line = bufferedReader.readLine()) != null) {
                    // STOP if there is an eof
                    if (line.contains("eof")) {
                        string.append(line, 0, line.indexOf("eof"));
                        builds.add(line);
                        break;
                    }
                    string.append(line + "\n");
                    builds.add(line);
                }

                // Print XML
                System.out.println("[Client]        - port:" + socket.getPort() + " \n\n" + string);

                // lamport clock +1
                int count = builds.get(0).indexOf("[LamportClock] ") + "[LamportClock] ".length();
                String lamClock = "";
                int len = builds.get(0).length();
                for(int i = count; i < len; i++){
                    char cond = builds.get(0).charAt(i);
                    if(cond != '\n') lamClock += builds.get(0).charAt(i);
                    else
                        break;
                }

                // Compare Lamport Clocks
                // Take the lamport clock with larger value to increment
                AggregationServer.lamClock = Math.max(Integer.parseInt(lamClock), AggregationServer.lamClock);
                AggregationServer.lamClock++;
                // print lamport clock
                System.out.println("[ATOM] Consumer - Lamport Clock: " + AggregationServer.lamClock);

                if((builds.size() > 2 && builds.get(1).length() < 3)){
                    // 500 INTERNAL SERVER ERROR
                    sendStatus = "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_INTERNAL_ERROR + " INTERNAL SERVER ERROR\n" + "[User-Agent]   Aggregation Server\n";
                }else if(builds.size() == 2){
                    // 204 NO CONTENT then 201 CREATED
                    sendStatus =  "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_NO_CONTENT + " NO CONTENT\n" + "[User-Agent]   Aggregation Server\n" +
                            "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_CREATED + " CREATED\n" + "[User-Agent]   Aggregation Server\n";
                }else{
                    // Detect the request type whether it is PUT or GET
                    if(builds.get(1).startsWith("PUT"))
                        sendStatus = respondToPUT(builds);
                    if(builds.get(1).startsWith("GET"))
                        sendStatus = respondToGET();
                    if(!builds.get(1).startsWith("GET") && !builds.get(1).startsWith("PUT"))
                        // 400 BAD REQUEST
                        sendStatus = "[Status]       HTTP/1.1 " + HttpURLConnection.HTTP_BAD_REQUEST + " BAD REQUEST\n" + "[User-Agent]   Aggregation Server\n";;
                }
                Writer writer = new OutputStreamWriter(socket.getOutputStream());

                // lamport clock ++
                AggregationServer.lamClock++;
                System.out.println("[ATOM] Consumer - SUCCESSFULLY SENT MESSAGE");
                System.out.println("[ATOM] Consumer - Lamport Clock: "+AggregationServer.lamClock);

                sendStatus = "[LamportClock] " + AggregationServer.lamClock + "\n" + sendStatus;
                writer.write(sendStatus);
                writer.flush();
                writer.close();
                System.out.println("[ATOM] Consumer - SUCCESSFULLY RESPONDED");
                bufferedReader.close();
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("[ATOM] Consumer - FAIL TO POP MESSAGE");
            }
        }

        // Get a updated feed from the ContentServer
        // Determine its existence
        public static boolean feed(String feed, String directory){
            File file = new File(directory);

            String pathName = System.getProperty("java.class.path") + "/contents/";
            File temp = new File(pathName+"example.txt");
            boolean existFlag = file.exists();

            try {
                // Create new one if it does not exist
                if (!existFlag) file.createNewFile();
                // if expires
                Calendar calendar = Calendar.getInstance();
                calendar.getTime();

                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());

                BufferedWriter output = new BufferedWriter(new FileWriter(temp));
                output.write("Last Update: " + formatter.format(date) + "\n");

                output.write(feed);
                output.flush();
                output.close();

                AggregationServer.lamClock++;
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Fail to feed update");
            }
            return existFlag;
        }

        // Check if the example feed expires
        public static boolean ifExpire(String input, int interval){
            Calendar cal = Calendar.getInstance();
            long tim = cal.getTimeInMillis();

            //The time of the last update will be displayed on the first line
            String updateTime = input.substring("Last Update: ".length(), input.length());
            if(tim-Long.parseLong(updateTime) > interval)
                return true;
            return false;
        }
    }
        // Waiting for Requests from Producer
        public void run() {
            try {
                do {
                    Socket socket = conQueue.take();
                    if (AggregationServer.response)
                        new consumerList(socket, lamClock).pop();
                } while (true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
