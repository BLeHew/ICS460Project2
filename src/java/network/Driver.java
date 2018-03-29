package network;
import java.util.*;

public class Driver {
    static Scanner clientOrServerInput = new Scanner(System.in);
    static String cliOrServ;

    public static void main(String args[]) {
       // clientOrServerMethod();
        /*
       Client client = new Client();
       Server server = new Server();
       */
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
