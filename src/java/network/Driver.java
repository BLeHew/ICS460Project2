package network;
import java.util.*;

public class Driver {
	//------------- public map for all nodes on network----
	public final static int CLIENTPORT = 9875;
    public final static int SERVERPORT = 9876;
	public final static int SERVERPROXYPORT = 9877;
	public final static int CLIENTPROXYPORT = 9878;
    //--------------end of network map --------------------

    static Scanner clientOrServerInput = new Scanner(System.in);
    static String cliOrServ;

    public static void main(String args[]) {
       // clientOrServerMethod();
       Client client = new Client();
       Server server = new Server();
       //Proxy proxy = new Proxy();


    }
    // TODO exception handling

    private static void clientOrServerMethod() {
        System.out.println("Enter 'client' to run client instance, or 'server' to run server instance. ");
        do {
            cliOrServ = clientOrServerInput.nextLine().toLowerCase();
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
