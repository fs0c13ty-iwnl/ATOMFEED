//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   ATOMFeed.java                       //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

// **************************************************************************************
// Class Name: ATOMFeed
// Set up a feed file for content server
// read from the local file, concert tp XML then send it to content server
// **************************************************************************************
public class ATOMFeed {
    public String content;
    public int Length;

    // Read local files and convert to XML format
    public ATOMFeed(String file){
        try{
            String pathname = System.getProperty("java.class.path") + "/contents/" + file + ".txt";
            String cont;
            int Length = 0;
            FileReader reader = new FileReader(pathname);
            BufferedReader bufferedReader = new BufferedReader(reader);
            ArrayList<String> feed = new ArrayList<>();

            while ((cont = bufferedReader.readLine()) != null) {
                Length++;
                feed.add(cont);
            }
            reader.close();
            bufferedReader.close();

            XMLParser parser = new XMLParser();
            String header1 = "<?xml version='1.0' encoding='iso-8859-1' ?>\n";
            String header2 = "<feed xml:lang='en-US' xmlns=\"http://www.w3.org/2005/Atom\">\n";

            this.content = header1 + header2 + parser.parse(feed) + "</feed>";
            this.Length = Length;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Server] ATOM FEED FAILED");
        }
    }
}