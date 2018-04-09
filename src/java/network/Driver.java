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

    public static void main(String args[]) {
       //clientOrServerMethod();
       //userEnterInterferencePercentage();
       Client client = new Client();
       Server server = new Server();

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
                    Client client = new Client();
                    return;
                case "server":
                    Server server = new Server();
                    return;
                default:
                    System.out.println("Invalid entry. \nEnter 'client' to run client instance, or 'server' to run server instance. ");
                    break;
            }
        }while(!cliOrServ.equals("client") || !cliOrServ.equals("server")) ;
    }
}