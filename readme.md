 //            Distributed Systems  Assignment 2
 //    Filename     -   readme.txt                    
 //    Author       -   Songzhe Li                          
 //    Student ID   -   a1767109                            
 //    E-mail       -   a1767109@student.adelaide.edu.au    

* This assignment contains the following files (plus a folder containing text files):
        ATOMFeed.java	            XMLParser.java
        AggregationServer.java		contents
		GETClient.java  			test.java
        ContentServer.java		    startServer.sh
        startClient.sh              makefile
        readme.txt

        in folder /contents there will be the following files:
        ATOMClient1.txt	            ATOMClient3.txt
        ATOMClient2.txt	            ATOMClient4.txt
        example.txt


* Test Instructions
 -   I will provide two methods to test in this assignment
        1. Automated testing
            1. Open two terminals, both in the assignment folder
            2. For terminal 1, enter the following command to START SERVER
                    >> ./startServer.sh
            3. For terminal 2, enter the following command to test one client interact with server
               (This also will be included in the general tests below)
                    >> ./startClient.sh
            4. Combined General tests: I have integrated some complex tests in test.java
               For general tests:
                    [1] press CTRL+C in terminal 1 to shut down server
                    [2] Restart server using command >> ./startServer.sh
                    [3] In terminal 2, run the command >> ./generalTests.sh
                    [4] This may take a while, please be patient
                    [5] See test summary in terminal

        2. Manual testing
            1. First we need to compile everything, by using makefile
                    >> make
            2. Use the command to release ports
                    >> kill -9 `lsof -t -i:4567`
            3. Use the following command to start server
                    java AggregationServer
            4. Use the following command to test GETClient
                    >> java -classpath . GETClient http://127.0.0.1:4567 ATOMClient/1/0
                        ** ATOMClient/1/0 can be replaced with any other client names
            5. Use the following command to test ContentServer
                    >> java -classpath . ContentServer http://127.0.0.1:4567 ATOMClient1
                        ** ATOMClient1 can be replaced with ATOMClient2 ATOMClient3 ATOMClient4
            6. Use the following command to run test.java, so to apply complex tests
                    >> java -classpath . test http://127.0.0.1:4567



 * Expected output
                  1. if everything goes OK, you should see a 200 OK or 201 CREATED for outcomes
                  2. Also, for general tests using the test.java script, you should expect PASS
                  for all four tests

 [IMPORTANT NOTE] Whenever you see a 'already in use' alert in terminal, you may use the following
                  to release port: >> kill -9 `lsof -t -i:4567`
