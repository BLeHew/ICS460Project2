package network;
import java.io.*;
import java.net.*;

import generators.*;
import helpers.*;
import packet.*;

public class Server {
    private static final String HOSTNAME = "localhost";
    private InetAddress IPAddress;
	private DatagramSocket serverSocket;
	private DatagramPacket request;
	private DatagramPacket response;

	private int startOffset = 0;
	private FileOutputStream fileStreamOut;


	private PacketGenerator packetGenerator;
	private byte[] receiveData = new byte[512];
	private final Proxy proxy;

	private int dataLength = receiveData.length - Packet.DATAHEADERSIZE;

	private final Window window;

	public Server(int windowSize, int interference, String ipAddress, int port) {

	    assignIPAddress(ipAddress);
	    createServerSocket(port);
	    proxy = new Proxy(interference);

	    window = new Window(windowSize);

	    Runnable r = new Runnable() {
	           @Override
	           public void run() {
	               runWork();
	           }
	       };
	       Thread t = new Thread(r);
	       t.setName("Server");
	       t.start();
	}
	private void assignIPAddress(String str) {
	    try {
            this.IPAddress = InetAddress.getByName(str);
        } catch ( UnknownHostException x ) {
            x.printStackTrace();
        }
    }
    private void runWork() {
        createFileStreamOut("receiveFile.txt");

        packetGenerator = new PacketGenerator(receiveData.length);

        boolean keepGoing = true;

        int expectedSeqNum = 1;

        do {

            request = packetGenerator.getResponsePacket(receiveData.length);
            receivePacketIntoSocket(request);

            if(verifyPacket()) {
                if(Data.getSeqNo(request) == expectedSeqNum) {
                    //System.out.print("RECV ");
                    adjustDataLength(Data.getLen(request));
                    writeDataToStream(request.getData());
                    //System.out.println(System.currentTimeMillis() + " " + Data.getSeqNo(request) + " RECV");
                    respondPositive();
                    expectedSeqNum++;
                }
                else {
                    System.out.println("DUPL " + System.currentTimeMillis() + " " +  Data.getSeqNo(request) + " !Seq");
                    respondPositive();
                }


            }

        } while ( keepGoing );

        serverSocket.close();
        System.out.println("[SERVER]: Server socket closed");
    }
    private void adjustDataLength(short len) {
        if (len > 0 && len - Packet.DATAHEADERSIZE < dataLength ) {
            dataLength = len - Packet.DATAHEADERSIZE;
        }
    }
    private void respondPositive() {
	    response = packetGenerator.getAckPacket(request);
	    //System.out.println("SENDing ACK " + Data.getAckNo(response) + " " +  System.currentTimeMillis() + " " + proxy.send(response, serverSocket));
	    proxy.send(response, serverSocket);
	}
	private boolean verifyPacket() {
	    //System.out.print(CheckSumTools.testChkSum(request) ? "" : ("CRPT " + System.currentTimeMillis() + " " +  Data.getAckNo(request) + "\n"));
	    return CheckSumTools.testChkSum(request);
	}
    private void writeDataToStream(byte[] recData) {
        try {
            fileStreamOut.write(recData, Packet.DATAHEADERSIZE, dataLength);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void receivePacketIntoSocket(DatagramPacket p) {
        try {
            serverSocket.receive(p);
        } catch ( IOException x ) {
            //Just wait
        }
    }

    private void createFileStreamOut(String fileOutName) {
        try {
            fileStreamOut = new FileOutputStream(fileOutName);
        } catch ( FileNotFoundException x ) {
            System.err.println("[SERVER] Problem on creating output stream.");
            x.printStackTrace();
        }
    }
    /**
     * Creates a new server socket at the designated port
     * @param port
     */
    private void createServerSocket(int port) {
        try {

            serverSocket = new DatagramSocket(port);
            System.out.println("[SERVER] Server socket started on port: " + serverSocket.getLocalPort());
        } catch ( SocketException x ) {
            System.err.println("[SERVER] Problem on creating server socket.");
            x.printStackTrace();
        }
    }
}