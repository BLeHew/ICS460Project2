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

	private final PacketWindow packetWindow;

	public Server(int windowSize, int interference, String ipAddress, int port) {

	    assignIPAddress(ipAddress);
	    createServerSocket(port);
	    proxy = new Proxy(interference);

	    packetWindow = new PacketWindow(windowSize);

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

        do {

            request = packetGenerator.getResponsePacket(receiveData.length);
            receivePacketIntoSocket(request);

            if(verifyPacket()) {
                packetWindow.add(request);
                printPacketInfo();
                respondPositive();

                if(packetWindow.alreadyHas(request)) {
                    resendAck(request);
                }
                if (packetWindow.isFull()) {
                    writeDataToStream(packetWindow);
                    packetWindow.clear();
                }
            }
            if(packetWindow.canBeWritten()) {
                writeDataToStream(packetWindow);
                packetWindow.clear();
                keepGoing = false;
            }

        } while ( keepGoing );

        serverSocket.close();
        System.out.println("[SERVER]: Server socket closed");
    }

    private void writeDataToStream(PacketWindow pw) {
        int length = pw.size();

        if(pw.containsEoF()) {
            length = pw.getEoFIndex();
        }
        for(int i = 0; i < length; i++) {
                if(pw.get(i) != null && PacketData.getLen(pw.get(i)) != 0) {
                    adjustDataLength(PacketData.getLen(pw.get(i)));
                    writeDataToStream(pw.get(i).getData());
                    }
                }
       }

    private void adjustDataLength(short len) {
        if ( len > 0 && len - Packet.DATAHEADERSIZE < dataLength ) {
            dataLength = len - Packet.DATAHEADERSIZE;
        }
    }
    private void resendAck(DatagramPacket p) {
        response = packetGenerator.getAckPacket(p);
        System.out.println("[SERVER]: Packet already received, resending ACK : " + PacketData.getAckNo(p));
        try {
            serverSocket.send(response);
        } catch ( IOException x ) {
            x.printStackTrace();
        }

    }
    private void respondPositive() {
	    response = packetGenerator.getAckPacket(request);

	    try {
            //serverSocket.send(proxy.interfere(response));
            serverSocket.send(response);
            System.out.println("[SERVER]:Ack Packet sent with ackNo of: " + PacketData.getAckNo(response));
            System.out.println("[SERVER]: Checksum: " + PacketData.getCkSum(response));
	    } catch ( IOException x ) {
            x.printStackTrace();
        }
	}
	private boolean verifyPacket() {

			//boolean retval =  //TODO method to calculate checksum from data, match?
	        System.out.print("[SERVER]: ");
	        System.out.println(CheckSumTools.testChkSum(request) ? "Packet is valid" : "Packet is invalid");
			return CheckSumTools.testChkSum(request);
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
            serverSocket.setSoTimeout(10000);
            serverSocket.receive(p);
        } catch ( IOException x ) {
            System.err.println("[SERVER] Waiting for packets...");
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