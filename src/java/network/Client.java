package network;
import java.io.*;
import java.net.*;
import java.util.*;

import generators.*;
import packet.*;


public class Client {
    static final String HOSTNAME = "localhost";
    private InetAddress IPAddress;
    private DatagramSocket clientSocket;

    //TODO allow user to designate the size of the packets to be sent
    private final int packetSize;
    private final int timeOut; // time to timeout the client socket
    //TODO allow user to determine the size of the window
    private final int windowSize;
    private final int port;
    //percentage of packets the client wants dropped, altered, or corrupted
    private final int interference;
    private final int delay; //delay time in seconds

    private Scanner inFromUser = new Scanner(System.in);

    private FileInputStream fileStreamIn;

    public Client(int packetSize, int timeOut, int windowSize, int port, int interference,
                  int delay) {
        this.packetSize = packetSize;
        this.timeOut = timeOut;
        this.windowSize = windowSize;
        this.port = port;
        this.interference = interference;
        this.delay = delay;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };
        Thread t = new Thread(r);
        t.setName("Client");
        t.start();
    }
    private void runWork() {
        assignIPAddress();
        System.out.println("[CLIENT]: Relative filepath to file you want client to send to server?");

        //keep attempting to create fileStream from user input
        while(!createFileStream(inFromUser.nextLine()));

        PacketGenerator pg = new PacketGenerator(fileStreamIn, packetSize, IPAddress,Driver.SERVERPORT);
        PacketSender packetSender = new PacketSender(pg, delay, interference, windowSize, port, timeOut);
        packetSender.start();
    }


    private boolean createFileStream(String filePath) {
        try {
            fileStreamIn = new FileInputStream(filePath);
            System.out.println("[CLIENT]: File stream created for: " + filePath);
            return true;
        } catch ( FileNotFoundException x ) {
            System.err.println("[CLIENT]: Unable to find file, please try again.");
            return false;
        }
    }
    private void assignIPAddress() {
        try {
            IPAddress = InetAddress.getByName(HOSTNAME);
        } catch ( UnknownHostException x ) {
            x.printStackTrace();
        }
    }
}