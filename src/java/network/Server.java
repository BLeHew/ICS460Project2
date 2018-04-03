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
	private byte[] receiveData = new byte[512];

	private int dataLength = receiveData.length - Packet.DATAPACKETHEADERSIZE;

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
        request = new DatagramPacket(receiveData, receiveData.length);


        while (true) {

                receivePacketIntoSocket(request);

                if (verifyPacket()) {

                    adjustDataLength(PacketData.getLen(request));

                    printPacketInfo();
                    packetNumber++ ;
                    writeDataToStream(receiveData);
                    respondPositive();
            }

        }
	}
	private void adjustDataLength(short len) {
	    if(len < dataLength || len > dataLength) {
            dataLength = len - Packet.DATAPACKETHEADERSIZE;
        }
    }
    private void respondPositive() {
	    response = packetGenerator.getAckPacket(request);
        response.setAddress(request.getAddress());
	    response.setPort(request.getPort());

	    try {
            serverSocket.send(response);
            System.out.println("[SERVER]: Packet sent with ackNo of: " + PacketData.getAckNo(response));
        } catch ( IOException x ) {
            x.printStackTrace();
        }
	}
	private boolean verifyPacket() {
			boolean retval = PacketData.getCkSum(request) == Packet.CHECKSUMGOOD; //TODO method to calculate checksum from data, match? 
			System.out.println("[SERVER]: CHECKSUM IS GOOD?: " + retval );
			System.out.println("[SERVER]: Packet is verified.");
			return retval;
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
            + "START:" + startOffset + " - END: "+ (startOffset += receiveData.length)
            + "\n"
            );
    }
    private void writeDataToStream(byte[] recData) {
        try {
            System.out.println("[SERVER]: writing " + dataLength + " bytes to a file");
            fileStreamOut.write(recData, Packet.DATAPACKETHEADERSIZE, dataLength);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void receivePacketIntoSocket(DatagramPacket p) {
        try {
            serverSocket.receive(p);
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