            Distributed Systems  Assignment 2
    Filename     -   readme.txt
    Author       -   Songzhe Li
    Student ID   -   a1767109
    E-mail       -   [a1767109@student.adelaide.edu.au](mailto:a1767109@student.adelaide.edu.au)
### Project Description
SEE DESCRIPTION IN description.md
### Test Instructions ###
I will provide two methods to test in this assignment

1. Automated testing
   1. Open two terminals, both in the assignment folder
   2. For terminal 1, enter the following command to START SERVER
            ./startServer.sh
   3. For terminal 2, enter the following command to test one client interact with server
      (This also will be included in the general tests below)
            ./startClient.sh
   4. Combined General tests: I have integrated some complex tests in test.java
      For general tests:<br>
      [1] press CTRL+C in terminal 1 to shut down server<br>
      [2] Restart server using command >> ./startServer.sh<br>
      [3] In terminal 2, run the command >> ./generalTests.sh<br>
      [4] This may take a while, please be patient<br>
      [5] See test summary in terminal<br>
2. Manual testing<br>
   1. First we need to compile everything, by using makefile<br>
            make<br>
   2. Use the command to release ports<br>
            kill -9 `lsof -t -i:4567`<br>
   3. Use the following command to start server<br>
            java AggregationServer<br>
   4. Use the following command to test GETClient<br>
            java -classpath . GETClient [http://127.0.0.1:4567](http://127.0.0.1:4567) ATOMClient/1/0<br>
       ** ATOMClient/1/0 can be replaced with any other client names<br>
   5. Use the following command to test ContentServer<br>
            java -classpath . ContentServer [http://127.0.0.1:4567](http://127.0.0.1:4567) ATOMClient1<br>
       ** ATOMClient1 can be replaced with ATOMClient2 ATOMClient3 ATOMClient4<br>
   6. Use the following command to run test.java, so to apply complex tests<br>
            java -classpath . test [http://127.0.0.1:4567](http://127.0.0.1:4567)<br>

* Expected output<br>
  
  1. if everything goes OK, you should see a 200 OK or 201 CREATED for outcomes<br>
  2. Also, for general tests using the test.java script, you should expect PASS for all four tests

### [IMPORTANT NOTE] Whenever you see a 'already in use' alert in terminal, you may use the following to release port: >> kill -9 `lsof -t -i:4567`
