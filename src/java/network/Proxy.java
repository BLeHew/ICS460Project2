package network;

import java.io.*;
import java.net.*;
import java.util.*;

import packet.*;


public class Proxy {

    private static final String HOSTNAME = "localhost";

    private InetAddress IPAddress;

	private DatagramSocket clientProxySocket;
	private DatagramSocket serverProxySocket;

	private DatagramPacket clientToProxyPacket;
	private DatagramPacket proxyToServerPacket;
	private DatagramPacket proxyToClientPacket;
	private DatagramPacket serverToProxyPacket;

	private int packetNumber = 0;
	private int startOffset = 0;
	private FileOutputStream fileStreamOut;


	private byte[] receiveData = new byte[500];

	public Proxy() {
	    Runnable r = new Runnable() {
	           @Override
	           public void run() {
	               runWork();
	           }
	       };
	       Thread t = new Thread(r);
	       t.setName("Proxy");
	       t.start();
	}
	private void runWork() {
	    createServerProxySocket();
	    createClientProxySocket();

        while (true) {
        	//ClientToServer
        		// receive from client logic
            clientToProxyPacket = new DatagramPacket(receiveData, receiveData.length);
            receivePacketIntoSocket(clientToProxyPacket);
            	packetNumber++;

            	//now get it ready to send to server.
	    		proxyToServerPacket = clientToProxyPacket;

			// send to server logic
	    		assignIPAddress();
	    		proxyToServerPacket.setAddress(IPAddress);
	    		proxyToServerPacket.setPort(Driver.SERVERPORT);
	    		try {
	    			System.out.println("[PROXY] About to send packet to server");
				serverProxySocket.send(proxyToServerPacket);
    				System.out.println("[PROXY] SENT packet to server");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

   			serverToProxyPacket = new DatagramPacket(receiveData, receiveData.length);
            receivePacketIntoSocket(serverToProxyPacket);
            	packetNumber++;

            	//now get it ready to send to client.
	    		proxyToClientPacket = serverToProxyPacket;

			// send to client logic
	    		proxyToClientPacket.setAddress(IPAddress);
	    		proxyToClientPacket.setPort(Driver.CLIENTPORT);
	    		try {
				clientProxySocket.send(proxyToClientPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	 /**
	 * Creates a new proxy socket at the designated port
	 * @param port
	 * @throws SocketException if the port is unavailable
	 */
    	private void createClientProxySocket() {
        try {
        		clientProxySocket = new DatagramSocket(Driver.CLIENTPROXYPORT);
            System.out.println("[PROXY] Client-Proxy socket started on port: " + clientProxySocket.getLocalPort());
        } catch ( SocketException x ) {
            System.err.println("[PROXY] Problem on creating client - proxy socket.");
            x.printStackTrace();
        }
    	}

    private void createServerProxySocket() {
        try {
        		serverProxySocket = new DatagramSocket(Driver.SERVERPROXYPORT);
            System.out.println("[PROXY] Server-Proxy socket started on port: " + serverProxySocket.getLocalPort());
        } catch ( SocketException x ) {
            System.err.println("[PROXY] Problem on creating server - proxy socket.");
            x.printStackTrace();
        }
    }

    private void receivePacketIntoSocket(DatagramPacket packet) {
        try {
            System.out.println("[PROXY] about to try to receive packet");
            clientProxySocket.receive(packet);
            System.out.println("[PROXY] received packet w/ data: " + packet.getData());
        } catch ( IOException x ) {
            System.err.println("[PROXY] Error receiving packet into socket.");
            x.printStackTrace();
        }
    }

    private void assignIPAddress() {
        try {
            IPAddress = InetAddress.getByName(HOSTNAME);
        } catch ( UnknownHostException x ) {
            x.printStackTrace();
        }
    }


    /*
     * BELOW HERE WE HAVE THE "FAULTY NETWORK" METHODS
     */

    // for now interference is random.
    // Implement user-controlled interference.
	private DatagramPacket randomInterference(DatagramPacket packet) {
		int rand1 = randomNumberGenerator();
		int rand2 = randomNumberGenerator();
		if(rand1==rand2) {
			 if(rand1<=2) {
					packet = changeByteInPacket(packet);
			 }else if (rand1 > 2 && rand1 <=5) {
					packet = dropByteFromPacket(packet);
			 }else if (rand1 > 5 && rand1 <=7){
					packet = makePacketDisappear(packet);
			 }else {
					packet = makePacketLate(packet);
			 }
		}
		return packet;
	}

	private DatagramPacket changeByteInPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		data[1]=(byte)(randomNumberGenerator());
		packet.setData(data);
		return packet;
	}

	private DatagramPacket dropByteFromPacket(DatagramPacket packet) {
		byte[] data = new byte[packet.getData().length - 1];

		PacketData.setCkSumBad(packet);
		packet.setData(data);
		return packet;
	}

	private DatagramPacket makePacketLate(DatagramPacket packet) {
		try {
			Thread.sleep(5000);//5 seconds
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packet;
	}

	private DatagramPacket makePacketDisappear(DatagramPacket packet){
		return null;
	}

	//must generate 1 bit number.
	private int randomNumberGenerator() {
		Random rand = new Random();
		int value = rand.nextInt(9);
		return value;
	}
}
