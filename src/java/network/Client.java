package network;
import java.io.*;
import java.net.*;
import java.util.*;

import generators.*;


public class Client {
    private final static int PORT = 9876;
    private static final String HOSTNAME = "localhost";
    private InetAddress IPAddress;

    private DatagramSocket clientSocket;

    private DatagramPacket responsePacket;
    private DatagramPacket sendPacket;

    //TODO allow user to determine the size of the window
    private DatagramPacket[] window = new DatagramPacket[5];

    private PacketGenerator packetGenerator;

    private Scanner inFromUser = new Scanner(System.in);

    private int packetNum = 0;
    private int totalPackets;
    private int startOffset = 0;

    private FileInputStream fileStreamIn;

    public Client() {
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

        createSocket();
        assignIPAddress();

        System.out.println("Relative filepath to file you want client to send to server?");

        //keep attempting to create fileStream from user input
        while(!createFileStream(inFromUser.nextLine()));

        //TODO allow user to designate the size of the packets to be sent
        int packetSize = 500;

        packetGenerator = new PacketGenerator(fileStreamIn, packetSize);

        totalPackets = packetGenerator.packetsLeft(); //get number of packets to send

        while(packetGenerator.hasMoreData()) {

                //get the next packet to be sent from the generator
                sendPacket = packetGenerator.getPacketToSend();
                sendPacket.setAddress(IPAddress);
                sendPacket.setPort(PORT);

                sendClientPacket();

                placePacketInWindow();

                waitForResponsePacket();



                packetNum++;
            }
        clientSocket.close();

    }

    private void placePacketInWindow() {

    }
    private void waitForResponsePacket() {
        try {
            clientSocket.receive(responsePacket);
        } catch ( IOException x ) {
            x.printStackTrace();
        }

    }
    private void sendClientPacket() {
        try {
            System.out.println("[CLIENT]: [SENDING]: " + packetNum + "/" + totalPackets);
            System.out.println("[CLIENT]: PACKET_OFFSET: "
                                                       + startOffset
                                                       + " - END: "
                                                       + (startOffset += sendPacket.getLength())
                                                       + "\n");
            clientSocket.send(sendPacket);
        }catch(IOException io) {
            System.err.println("[CLIENT]: Error in sending packet");
        }
    }
    private boolean createFileStream(String filePath) {
        try {
            fileStreamIn = new FileInputStream(filePath);
            return true;
        } catch ( FileNotFoundException x ) {
            System.err.println("Unable to find file, please try again.");
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
    private void createSocket() {
        try {
            clientSocket = new DatagramSocket();
        } catch ( SocketException x ) {
            x.printStackTrace();
        }
    }

}
