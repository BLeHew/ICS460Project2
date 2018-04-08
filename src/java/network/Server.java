package network;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

import generators.*;
import packet.*;

public class Server {
    private static final String HOSTNAME = "localhost";
    private static final int PACKETSIZE = 512;
    private static final int PACKETWINDOWSIZE = 2;
    private int expectedPacketNo = 1; //the first packet should have seqNo == 1
    private LinkedList<DatagramPacket> parkingLot = new LinkedList<DatagramPacket>();
    private InetAddress IPAddress;
	private DatagramSocket serverSocket;
	private DatagramPacket request;
	private DatagramPacket response;

	private int startOffset = 0;
	private FileOutputStream fileStreamOut;
	private PacketGenerator packetGenerator;
	private byte[] receiveData = new byte[PACKETSIZE];

	private int dataLength = receiveData.length - Packet.DATAHEADERSIZE;

	private PacketWindow packetWindow;

	//private int dataLength = receiveData.length - Packet.DATAHEADERSIZE;

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
        packetWindow = new PacketWindow(PACKETWINDOWSIZE);
        packetGenerator = new PacketGenerator(receiveData.length);

        while (true) {

            while(!packetWindow.isFull()) {
                request = packetGenerator.getResponsePacket(receiveData.length);
                receivePacketIntoSocket(request);
                System.out.println("Packet length: " + PacketData.getLen(request));
                System.out.print("[Server]: ");
                
                //for each element in the parking lot, is it the next expected packet? 
                for (DatagramPacket ele : parkingLot) {
                		System.out.println("Checking the parking lot");
                		if (PacketData.getSeqNo(ele) == expectedPacketNo ) {
                			packetWindow.add(ele);
                			parkingLot.remove(ele);
                			expectedPacketNo = PacketData.getSeqNo(ele) + 1;
                		}
                		if (packetWindow.isFull()) {
                				packetWindow.print();
                            writeDataToStream(packetWindow);
                            packetWindow.clear();
                		}
                }
                //if the packet just received is the expected packet
                if (PacketData.getSeqNo(request) == expectedPacketNo) {
                    packetWindow.add(request);
                    expectedPacketNo = PacketData.getSeqNo(request) + 1;
                }
                else{
                		parkingLot.add(request);	
                }
                printPacketInfo();
                respondPositive();
            }
            packetWindow.print();
            writeDataToStream(packetWindow);
            packetWindow.clear();
        }
	}

	
    private void writeDataToStream(PacketWindow pw) {
        pw.print();
        for(int i = 0; i < pw.size(); i++) {
                adjustDataLength(PacketData.getLen(pw.get(i)));
                writeDataToStream(pw.get(i).getData());
            }

        }
    private void adjustDataLength(short len) {
        System.out.println("len: " + len);
        System.out.println("len - Packet.Dataheadersize: " + (len - Packet.DATAHEADERSIZE));
	    //if(len - Packet.DATAHEADERSIZE < dataLength || len > dataLength) {

            dataLength = len - Packet.DATAHEADERSIZE;
        //}
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
			//System.out.println("[SERVER]: CHECKSUM IS GOOD?: " + retval );
			//System.out.println("[SERVER]: Packet is verified.");
			return retval;
	}
	private void printPacketInfo() {
        System.out.println("\n[SERVER]: PACKET RECEIVED. INFO: \n"
            + "[SERVER]: PACKET_NUMBER: "
            + PacketData.getSeqNo(request)
            + "\n"
            + "[SERVER]: PACKET_LENGTH: "
            + PacketData.getLen(request)
            + "\n"
            + "[SERVER]: PACKET_OFFSET: "
            + "START:" + startOffset + " - END: "+ (startOffset += PacketData.getLen(request))
            + "\n"
            );
    }
    private void writeDataToStream(byte[] recData) {
        try {
            System.out.println("[SERVER]: writing " + dataLength + " bytes to a file");
            fileStreamOut.write(recData, Packet.DATAHEADERSIZE, dataLength);
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