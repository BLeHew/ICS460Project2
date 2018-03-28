package network;
import java.io.*;
import java.net.*;

public class Server {
	private final static int PORT = 9876;
	private DatagramSocket serverSocket;
	private DatagramPacket request;

	private int packetNumber = 0;
	private int startOffset = 0;
	private FileOutputStream fileStreamOut;


	private byte[] receiveData = new byte[1024];

	public Server() {
	    Runnable r = new Runnable() {
	           @Override
	           public void run() {
	               runWork();
	           }
	       };
	       Thread t = new Thread(r);
	       t.start();
	}
	private void runWork() {
	    createServerSocket(PORT);
        createFileStreamOut("receiveFile.jpg");

        while (true) {
                createRequestPacket();
                receivePacketIntoSocket();
                if (verifyPacket()) {
                		respondPositive();
                		printPacketInfo();
                		packetNumber++;
                		writeDataToStream();
                }else {
                		respondNegative();
                }
        }
    }
    private void respondNegative() {
		// TODO Auto-generated method stub

	}
	private void respondPositive() {
		// TODO Auto-generated method stub

	}
	private boolean verifyPacket() {
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
    private void writeDataToStream() {
        try {
            fileStreamOut.write(receiveData, 0, receiveData.length);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void receivePacketIntoSocket() {
        try {
            serverSocket.receive(request);
        } catch ( IOException x ) {
            System.err.println("Error receiving packet into socket.");
            x.printStackTrace();
        }
    }
    private void createRequestPacket() {
        request = new DatagramPacket(receiveData, receiveData.length);
    }
    private void createFileStreamOut(String fileOutName) {
        try {
            fileStreamOut = new FileOutputStream(fileOutName);
        } catch ( FileNotFoundException x ) {
            System.err.println("Problem on creating output stream.");
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
            System.out.println("Server Started on port: " + PORT);
        } catch ( SocketException x ) {
            System.err.println("Problem on creating server socket.");
            x.printStackTrace();
        }
    }
}
