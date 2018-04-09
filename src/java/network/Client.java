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




    //TODO allow user to designate the size of the packets to be sent
    private int packetSize = 50;
    private int timeOut = 2000; // time to timeout the client socket
    //TODO allow user to determine the size of the window
    private int windowSize = 5;

  //percentage of packets the client wants dropped, altered, or corrupted
    private int interference = 80;

    private PacketWindow packetWindow;
    private PacketGenerator packetGenerator;
    private Proxy proxy;

    private Scanner inFromUser = new Scanner(System.in);

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
        createSocket(Driver.CLIENTPORT);
        assignIPAddress();
        System.out.println("[CLIENT]: Relative filepath to file you want client to send to server?");

        //keep attempting to create fileStream from user input


        while(!createFileStream(inFromUser.nextLine()));



        packetGenerator = new PacketGenerator(fileStreamIn, packetSize, IPAddress, Driver.SERVERPORT);

        totalPackets = packetGenerator.packetsLeft() + 1; //get number of packets to send

        packetWindow = new PacketWindow(windowSize);

        proxy = new Proxy(interference);

        while(packetGenerator.hasMoreData()) {

                 //keep sending packet while the packetwindow isn't full and the packetgenerator has more packets to send.
                 while(!packetWindow.isFull() && packetGenerator.hasMoreData()) {

                    sendPacket = packetGenerator.getPacketToSend();
                    packetWindow.add(sendPacket);

                    sendPacketFromClient(proxy.interfere(sendPacket));

                    System.out.println("[CLIENT]: Sent packet with len: " + PacketData.getLen(sendPacket));
                    System.out.println("[CLIENT]: Sent packet with SeqNO: " + PacketData.getSeqNo(sendPacket));
                    delayForSimulation(1000);   //time in milliseconds for simulation
                 }

                while(packetWindow.hasPackets()) {
                    if(!waitForResponsePacket()) {
                        for(int i = 0; i < packetWindow.size(); i++) {
                            if(packetWindow.get(i) != null) {
                                resendPacketFromClient(proxy.interfere(packetWindow.get(i)));
                            }
                        }
                    }
                }
        }
        //Send the End of File packet, signalling the end of the stream
        sendPacket = packetGenerator.getEoFPacket();
        sendPacketFromClient(sendPacket);

        clientSocket.close();
        System.out.println("\n[CLIENT] Client socket closed");
    }
    private void resendPacketFromClient(DatagramPacket p) {
        System.out.println("[CLIENT]: [RESENDING] : " + PacketData.getSeqNo(p) + " /" + totalPackets);
        try {
            clientSocket.send(p);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void delayForSimulation(int i) {
        try {
            Thread.sleep(i);
        } catch ( InterruptedException x ) {
            x.printStackTrace();
        }
    }
    private boolean waitForResponsePacket() {
        System.out.println("[CLIENT]: ....waiting for response packet...");
        try {
            	responsePacket = packetGenerator.getResponsePacket(Packet.ACKPACKETHEADERSIZE);
        		clientSocket.receive(responsePacket);
        		packetWindow.remove(responsePacket);
        		return true;
        } catch ( IOException x ) {
            return false;
        }
    }
    private void sendPacketFromClient(DatagramPacket packetToSend) {
        /*
        System.out.println("[CLIENT]: [SENDING]: " + PacketData.getSeqNo(sendPacket) + "/" + totalPackets);
        System.out.println("[CLIENT]: PACKET_OFFSET: " + startOffset + " - END: "
                                                   + (startOffset += sendPacket.getLength())
                                                   + "\n");
        System.out.println("[CLIENT]:  packet ACK: " + PacketData.getAckNo(sendPacket));
        System.out.println("[CLIENT]:  packet Len: " + PacketData.getLen(sendPacket));
        */
        try {
            clientSocket.send(packetToSend);
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
            clientSocket.setSoTimeout(timeOut);
            System.out.println("[CLIENT]: Client socket started on port: " + clientSocket.getLocalPort());
        }catch ( SocketException x ) {
        System.err.println("[CLIENT]: Problem on creating client socket.");
        x.printStackTrace();
        }
    }
}