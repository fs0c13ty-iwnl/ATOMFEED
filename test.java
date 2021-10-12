//*********************************************************//
//            Distributed Systems  Assignment 2            //
//    Filename     -   test.java                           //
//    Author       -   Songzhe Li                          //
//    Student ID   -   a1767109                            //
//    E-mail       -   a1767109@student.adelaide.edu.au    //
//*********************************************************//

// **************************************************************************************
// Class Name: test.java
// Start a series of complex tests, print the summary at the end
// **************************************************************************************
public class test {
    static boolean end1 = false;
    static boolean success1 = false;
    static boolean end2 = false;
    static boolean success2 = false;
    static boolean end3 = false;
    static boolean success3 = false;
    static boolean end4 = false;
    static boolean success4 = false;

    public static void main(String[] args) throws Exception {
        String address = args[0];
        boolean end = false;    // if reaches the end of testing

        // A synchronized lock to ensure only one test can operate at a time
        Thread t1 = new Thread(() -> {
            test1(address);
        });
        t1.start();

        Thread.sleep(1000);
        Thread t2 = new Thread(() -> {
            test2(address);
        });
        t2.start();

        Thread.sleep(1000);
        Thread t3 = new Thread(() -> {
            test3(address);
        });
        t3.start();

        Thread.sleep(1000);
        Thread t4 = new Thread(() -> {
            test4(address);
        });
        t4.start();

        // Join testing
        t1.join();
        t2.join();
        t3.join();
        t4.join();

        while (true) {
            if (end1) {
                t1.stop();
            }

            if (end2) {
                t2.stop();
            }

            if (end3) {
                t3.stop();
            }

            if (end4) {
                t4.stop();
            }

            if (end1 && end2 && end3 && end4) {
                System.out.println("************************* Test SUMMARY *************************");
                if (success1)
                    System.out.println("[test1] PASS");
                else
                    System.out.println("[test1] FAIL");

                if (success2)
                    System.out.println("[test2] PASS");
                else
                    System.out.println("[test2] FAIL");

                if (success3)
                    System.out.println("[test3] PASS");
                else
                    System.out.println("[test3] FAIL");

                if (success4)
                    System.out.println("[test4] PASS");
                else
                    System.out.println("[test4] FAIL");

                System.exit(0);
            }
        }

    }

    // single_Client_and_Server
    public static synchronized void test1(String addr) {
        try {
            System.out.println("************************* Test 1 START *************************");

            ContentServer.startRequest(addr, "ATOMClient1");
            Thread.sleep(600);
            GETClient.startRequest(addr, "ATOMClient");
            Thread.sleep(400);

            System.out.println("************************** Test 1 END **************************\n");
            end1 = true;
            success1 = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test 1 FAILED");
            success1 = false;
            end1 = true;
        }

    }

    // single_Client_timeout
    public static synchronized void test2(String addr) {
        try {
            System.out.println("************************* Test 2 START *************************");

            ContentServer.startRequest(addr, "ATOMClient2");
            Thread.sleep(12000);
            GETClient.startRequest(addr, "ATOMClient");
            Thread.sleep(400);

            System.out.println("************************** Test 2 END **************************\n");
            end2 = true;
            success2 = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test 2 FAILED");
            success2 = false;
            end2 = true;
        }

    }

    // multiple_Client_and_Server
    public static synchronized void test3(String addr) {
        try {
            System.out.println("************************* Test 4 START *************************");
            for (int i = 1; i <= 3; i++) {
                ContentServer.startRequest(addr, "ATOMClient" + i);
            }
            Thread.sleep(1000);
            // file client
            for (int i = 1; i <= 3; i++) {
                GETClient.startRequest(addr, "ATOMClient");
            }

            Thread.sleep(300);
            System.out.println("************************** Test 4 END **************************\n");
            end3 = true;
            success3 = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test 3 FAILED");
            success3 = false;
            end3 = true;
        }
    }

    // multiple_Client_and_Server_timeout
    public static synchronized void test4(String addr){
        try{
            System.out.println("************************* Test 3 START *************************");
            ContentServer.startRequest(addr,"ATOMClient3");
            Thread.sleep(15000);
            for(int i=1;i<3;i++){
                ContentServer.startRequest(addr,"ATOMClient"+i);
            }
            Thread.sleep(2500);
            for(int i=0;i<3;i++){
                GETClient.startRequest(addr,"ATOMClient");
            }

            Thread.sleep(400);
            System.out.println("************************** Test 3 END **************************\n");
            end4 = true;
            success4 = true;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Test 4 FAILED");
            success4 = false;
            end4 = true;
        }
    }
}