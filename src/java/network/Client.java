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
    private PacketWindow packetWindow = new PacketWindow(1);

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
        System.out.println("[CLIENT]: Relative filepath to file you want client to send to server?");

        //keep attempting to create fileStream from user input
        while(!createFileStream(inFromUser.nextLine()));

        //TODO allow user to designate the size of the packets to be sent
        packetSize = 50;
        packetGenerator = new PacketGenerator(fileStreamIn, packetSize);
        totalPackets = packetGenerator.packetsLeft(); //get number of packets to send


        while(packetGenerator.hasMoreData()) {

                delayForSimulation(1000);   //time in milliseconds for simulation


                while(packetWindow.hasPackets()) {
                    if(!waitForResponsePacket()) {
                        for(int i = 0; i < packetWindow.size() ; i++) {
                            sendPacketFromClient(packetWindow.get(i));
                        }
                    }

                }
                sendPacket = packetGenerator.getPacketToSend();
                sendPacket.setAddress(IPAddress);
                sendPacket.setPort(Driver.SERVERPORT);
                sendPacketFromClient(sendPacket);
                packetWindow.add(sendPacket);
        }

        clientSocket.close();
        System.out.println("[CLIENT] Client socket closed");
    }
    private void delayForSimulation(int i) {
        try {
            Thread.sleep(i);
        } catch ( InterruptedException x ) {
            x.printStackTrace();
        }
    }
    private boolean waitForResponsePacket() {
        try {
            	responsePacket = packetGenerator.getResponsePacket(Packet.ACKPACKETHEADERSIZE);
            	clientSocket.setSoTimeout(2000);
        		clientSocket.receive(responsePacket);
        		packetWindow.remove(responsePacket);
        		System.out.println("[CLIENT] Ack packet received with ack of: " + PacketData.getAckNo(responsePacket));
        		return true;
        } catch ( IOException x ) {
            return false;
        }
    }
    private void sendPacketFromClient(DatagramPacket packetToSend) {
        try {

            System.out.println("[CLIENT]: [SENDING]: " + packetNum + "/" + totalPackets);
            System.out.println("[CLIENT]: PACKET_OFFSET: " + startOffset + " - END: "
                                                       + (startOffset += sendPacket.getLength())
                                                       + "\n");
            System.out.println("[CLIENT]:  packet Seq: " + PacketData.getSeqNo(sendPacket));
            System.out.println("[CLIENT]:  packet ACK: " + PacketData.getAckNo(sendPacket));
            System.out.println("[CLIENT]:  packet Len: " + PacketData.getLen(sendPacket));
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
    private void createSocket(int port) {
        try {
            clientSocket = new DatagramSocket(port);
            System.out.println("[CLIENT]: Client socket started on port: " + clientSocket.getLocalPort());
        }catch ( SocketException x ) {
        System.err.println("[CLIENT]: Problem on creating client socket.");
        x.printStackTrace();
        }
    }
}