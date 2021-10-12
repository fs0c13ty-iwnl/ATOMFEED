//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   XMLParser.java                      //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//
import java.util.ArrayList;

// **************************************************************************************
// Class Name: XMLParser
// Parse XML to TXT then validate the result
// **************************************************************************************
public class XMLParser {
    public String text;
    // **********************************************************************************
    // Read each line in the txt, then append the result to output
    //    The list of ATOM elements that you need to support are:
    //    title
    //    subtitle
    //    link
    //    updated
    //    author
    //    name
    //    id
    //    entry
    //    summary
    // **********************************************************************************
    public String parse(ArrayList<String> feed) {
        StringBuilder contentXML = new StringBuilder();
        int count = 0;
        while (count < feed.size()) {
            // When there is a new entry starts, it must start with "entry"
            if(feed.get(count).equals("entry")) {
                contentXML.append("        <entry>\n");

                for(int i = count + 1; i < feed.size(); i++) {
                    String currentFeed = feed.get(i);
                    String[] separate = currentFeed.split(":", 2);

                    switch (separate[0]) {
                        case "title":
                                contentXML.append("        <" + "title" + ">"
                                +currentFeed.substring(6)+
                                "</" + "title" + ">\n");
                                break;

                        case "subtitle":
                                contentXML.append("        <" + "subtitle" + ">"
                                +currentFeed.substring(9)+
                                "</" + "subtitle" + ">\n");
                                break;
                                
                        case "link":
                                contentXML.append("        <" + "link" + ">"
                                +currentFeed.substring(5)+
                                "</" + "link" + ">\n");
                                break;

                        case "updated":
		                        contentXML.append("        <" + "updated" + ">"
		                        +currentFeed.substring(8)+
		                        "</" + "updated" + ">\n");
		                        break;

                        case "author":
                                contentXML.append("        <author>\n" + "                <name>"
                                +currentFeed.substring(7)+
                                "</name>\n" + "        </author>\n");
                                break;

                        case "id":
                                contentXML.append("        <" + "id" + ">"
                                +currentFeed.substring(3)+
                                "</" + "id" + ">\n");
                                break;

                        case "summary":
                                contentXML.append("        <" + "summary" + ">"
                                +currentFeed.substring(8)+
                                "</" + "summary" + ">\n");
                                break;
                      
                    }
                    if(currentFeed.startsWith("entry")){
                        count = i - 1;
                        break;
                    }
                    if(i == feed.size() - 1)
                        count = i;
                }
                contentXML.append("        </entry>\n");
            }
            count++;
        }
        String result = contentXML.toString();
        return result;
    }

    // Lint the XML feed
    public boolean XML_linting(ArrayList<String> digest) {
        try{
            // Validate the feed on Client side
            String topLine = "<?xml version='1.0' encoding='iso-8859-1' ?>";
            boolean validationTop = !digest.get(0).equals(topLine);
            String secondLine = "<feed xml:lang='en-US' xmlns=\"http://www.w3.org/2005/Atom\">";
            boolean validationSec = !digest.get(1).equals(secondLine);
            String bottomLine = "</feed>";
            boolean validationBottom = digest.get(digest.size()-1).equals(bottomLine);

            if ( validationTop || validationSec || validationBottom ) {
                return false;
            }

            StringBuilder text = new StringBuilder();
            // EXCLUDE the line first and second and last
            int j = 2;
            while(j < digest.size() - 1){
                String bodyContent = digest.get(j);

                if(bodyContent.contains("<entry>"))
                    text.append("entry\n");

                else if(bodyContent.contains("<title>") && bodyContent.contains("</title>"))
                    text.append(parseTag(bodyContent, "title"));

                else if(bodyContent.contains("<subtitle>") && bodyContent.contains("</subtitle>"))
                    text.append(parseTag(bodyContent, "subtitle"));

                else if(bodyContent.contains("<link>") && bodyContent.contains("</link>"))
                    text.append(parseTag(bodyContent, "link"));

                else if(bodyContent.contains("<updated>") && bodyContent.contains("</updated>"))
                    text.append(parseTag(bodyContent, "updated"));

                else if(bodyContent.contains("<id>") && bodyContent.contains("</id>"))
                    text.append(parseTag(bodyContent, "id"));

                else if(bodyContent.contains("<summary>") && bodyContent.contains("</summary>"))
                    text.append(parseTag(bodyContent, "summary"));

                else if(bodyContent.contains("<author>")) {
                    j++;
                    text.append(parseAuthor(digest.get(j)));
                    j++;
                }
                j++;
            }
            this.text = text.toString();
            // Valid
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            // Invalid
            return false;
        }
    }

    // XML -> txt : tag
    public static String parseTag(String input, String tag) {
        String result = "";
        result = result + tag + ":"+input.substring(input.indexOf("<"+tag+">") + tag.length() + 2, input.indexOf("</"+tag+">"))+"\n";

        return result;
    }

    // XML -> txt : author
    public static String parseAuthor(String input) {
        String result = "";
        result = result + "author:" + input.substring(input.indexOf("<name>") + "name".length() + 2, input.indexOf("</name>")) + "\n";

        return result;
    }
}