package network;
import java.util.*;

public class Driver {
	//------------- public map for all nodes on network----
	public final static int CLIENTPORT = 9875;
    public final static int SERVERPORT = 9876;
    //--------------end of network map --------------------

    static Scanner userInput = new Scanner(System.in);
    static String cliOrServ;
    public static int INTERFERENCE_PERCENTAGE = 10;

    @SuppressWarnings("unused")
    public static void main(String args[]) {
        // clientOrServerMethod();
        // userEnterInterferencePercentage();

        /* Client Constructor parameters:
         * int packetSize -- the size of each packet sent, 1-500.
         * int timeOut -- the duration before the client socket times out, in milliseconds
         * int windowSize -- the size of the window that the packets are being placed in, suggest - 5
         * int port -- the port that this socket is assigned to
         * int interference -- the percentage of packets that will be dropped or corrupted
         * int delay -- the delay time in milliseconds of the simulation
         */
       Client client = new Client(500,2000,1,Driver.CLIENTPORT, 0, 1000);

       /*
        * Server Constructor parameters:
        * int windowSize -- see client
        * int interference -- see client
        * InetAddress iPAddress -- the iPAddress to set this server to.
        * int port -- see client
        */

       Server server = new Server(1,0,"localhost",Driver.SERVERPORT);

       // clientOrServerMethod();
    		//userEnterInterferencePercentage();
    		//Proxy proxy = new Proxy();
    }
    private static void userEnterInterferencePercentage() {
	    	do {
	    		System.out.println("Enter any int 0-100 percentage of packets to be intefered with: ");
	    		INTERFERENCE_PERCENTAGE = userInput.nextInt();
	    	}while(!(INTERFERENCE_PERCENTAGE >= 0) || !(INTERFERENCE_PERCENTAGE <= 100)) ;
	}

    private static void clientOrServerMethod() {
        System.out.println("Enter 'client' to run client instance, or 'server' to run server instance. ");
        do {
            cliOrServ = userInput.nextLine().toLowerCase();
            switch (cliOrServ) {
                case "client":
                   // Client client = new Client();
                    return;
                case "server":
                    //Server server = new Server();
                    return;
                default:
                    System.out.println("Invalid entry. \nEnter 'client' to run client instance, or 'server' to run server instance. ");
                    break;
            }
        }while(!cliOrServ.equals("client") || !cliOrServ.equals("server")) ;
    }
}