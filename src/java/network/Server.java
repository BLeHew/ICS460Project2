package network;
import java.io.*;
import java.net.*;

import generators.*;
import packet.*;

public class Server {
    private static final String HOSTNAME = "localhost";
    private InetAddress IPAddress;
	private DatagramSocket serverSocket;
	private DatagramPacket request;
	private DatagramPacket response;

	private int packetNumber = 0;
	private int startOffset = 0;
	private FileOutputStream fileStreamOut;
	private PacketGenerator packetGenerator;
	private byte[] receiveData = new byte[500];

	public Server() {
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
	private void runWork() {
	    createServerSocket(Driver.SERVERPORT);
        createFileStreamOut("receiveFile.txt");
        packetGenerator = new PacketGenerator(receiveData.length);
        while (true) {
            request = new DatagramPacket(receiveData, receiveData.length);
            receivePacketIntoSocket();

            if (verifyPacket()) {
                System.out.println("[SERVER] Packet is verified.");
            		printPacketInfo();
            		packetNumber++;
            		writeDataToStream(receiveData, 0);
            		respondPositive();
            }
        }
	}
	private void respondPositive() {
	    response = packetGenerator.getAckPacket(request);
	    System.out.println("[SERVER] responding positive");
//	    System.out.println("[SERVER] Request address: " + request.getAddress());
//	    System.out.println("[SERVER] Request port: "  + request.getPort());
	    response.setAddress(request.getAddress());
	    response.setPort(Driver.CLIENTPORT);
	   // response.setPort(Driver.SERVERPROXYPORT); TODO REIMPLEMENT PROXY RESPONSE

	    try {
            serverSocket.send(response);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
	}
	private boolean verifyPacket() {
			boolean retval = PacketData.getCkSum(request) == Packet.CHECKSUMGOOD;
			System.out.println("[SERVER] CHECKSUM IS GOOD?: " + retval );
			System.out.println("[SERVER] Regardless we return true");
			//return retval;
			return true;
	}
	private void printPacketInfo() {
        System.out.println("\n[SERVER]: PACKET RECEIVED. INFO: \n"
            + "[SERVER]: PACKET_NUMBER: "
            + packetNumber
            + "\n"
            + "[SERVER]: PACKET_LENGTH: "
            + request.getLength()
            + "\n"
            + "[SERVER]: PACKET_OFFSET: "
            + "START:" + startOffset + " - END: "+ (startOffset += request.getLength())
            + "\n"
            );
    }
    private void writeDataToStream(byte[] recData, int offset) {
        try {
            fileStreamOut.write(recData, 0, recData.length);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void receivePacketIntoSocket() {
        try {
            serverSocket.receive(request);
        } catch ( IOException x ) {
            System.err.println("[SERVER] Error receiving packet into socket.");
            x.printStackTrace();
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
     * @throws SocketException if the port is unavailable
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