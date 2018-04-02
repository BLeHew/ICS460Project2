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
    private DatagramPacket responsePacket; //response from server
    private DatagramPacket sendPacket; // packet to send to server

    //TODO allow user to determine the size of the window
    private PacketWindow packetWindow = new PacketWindow(5);

    private PacketGenerator packetGenerator;

    private Scanner inFromUser = new Scanner(System.in);

    private int packetNum = 0;
    private int totalPackets;
    private int startOffset = 0;
    private int packetSize;

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
        createSocket(Driver.CLIENTPORT);
        assignIPAddress();
        System.out.println("Relative filepath to file you want client to send to server?");

        //keep attempting to create fileStream from user input
        while(!createFileStream(inFromUser.nextLine()));

        //TODO allow user to designate the size of the packets to be sent
        packetSize = 500;
        packetGenerator = new PacketGenerator(fileStreamIn, packetSize);
        totalPackets = packetGenerator.packetsLeft(); //get number of packets to send


        while(packetGenerator.hasMoreData()) {
                sendPacket = packetGenerator.getPacketToSend();
                sendPacket.setAddress(IPAddress);
                sendPacket.setPort(Driver.SERVERPORT);

                if(!packetWindow.isFull()) {
                     sendPacketFromClient(sendPacket);
                     packetWindow.add(sendPacket);
                }
                else {
                    waitForResponsePacket();
                    packetWindow.remove(responsePacket);
                }
                packetNum++;
        }
        while (!packetWindow.isEmpty()) {
        	 	waitForResponsePacket();
        	 	packetWindow.remove(responsePacket);
        }
        clientSocket.close();
        System.out.println("[CLIENT] Client socket closed");
    }
    private void waitForResponsePacket() {
        try {
                responsePacket = packetGenerator.getResponsePacket(Packet.ACKPACKETHEADERSIZE);
        		clientSocket.receive(responsePacket);
            	responsePacket = packetGenerator.getResponsePacket(Packet.ACKPACKETHEADERSIZE);
        		clientSocket.receive(responsePacket);
        		System.out.println("[CLIENT] Ack packet received with ack of: " + PacketData.getAckNo(responsePacket));
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void sendPacketFromClient(DatagramPacket packetToSend) {
        try {

            System.out.println("[CLIENT]: [SENDING]: " + packetNum + "/" + totalPackets);
            System.out.println("[CLIENT]: PACKET_OFFSET: "
                                                       + startOffset
                                                       + " - END: "
                                                       + (startOffset += sendPacket.getLength())
                                                       + "\n");
            System.out.println("Client:  packet port " + sendPacket.getPort());
            System.out.println("Client:  packet addr " + sendPacket.getAddress());
            System.out.println("Client:  packet ACK: " + PacketData.getAckNo(sendPacket));

            clientSocket.send(sendPacket);
            //Proxy proxy = new Proxy();
            //clientSocket.send(proxy.interfere(packetToSend));
        }catch(IOException io) {
            System.err.println("[CLIENT]: Error in sending packet");
        }
    }
    private boolean createFileStream(String filePath) {
        try {
            fileStreamIn = new FileInputStream(filePath);
            System.out.println("File stream created for: " + filePath);
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
    private void createSocket(int port) {
        try {
            //clientSocket = new DatagramSocket();
            clientSocket = new DatagramSocket(port);
            System.out.println("[CLIENT] Client socket started on port: " + clientSocket.getLocalPort());
        }catch ( SocketException x ) {
        System.err.println("[CLIENT} Problem on creating client socket.");
        x.printStackTrace();
        }
    }
}