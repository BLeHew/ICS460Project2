package packet;

import java.io.*;
import java.net.*;

import generators.*;
import network.Proxy;

public class PacketSender{
    private final PacketGenerator packetGenerator;
    private final Proxy proxy;
    private final PacketWindow packetWindow;
    private DatagramSocket socket;
    private final int delayTime;

    private final int totalPackets;
    private int startOffset = 0;

    private DatagramPacket sendPacket;
    private DatagramPacket responsePacket;


    public PacketSender(PacketGenerator packetGenerator,int delayTime,int interference, int windowSize,
                        int port,int timeOut) {
        this.packetGenerator = new PacketGenerator(packetGenerator);
        this.proxy =  new Proxy(interference);
        this.packetWindow = new PacketWindow(windowSize);
        totalPackets = packetGenerator.packetsLeft() + 1;
        this.delayTime = delayTime;
        createSocket(port,timeOut);
    }
    public void start() {
        while(packetGenerator.hasMoreData()) {
                 //keep sending packet while the packetwindow isn't full and the packetgenerator has more packets to send.
                 while(!packetWindow.isFull() && packetGenerator.hasMoreData()) {

                    sendPacket = packetGenerator.getPacketToSend();
                    packetWindow.add(sendPacket);

                    sendPacket(proxy.interfere(sendPacket));

                    delayForSimulation(delayTime);
                 }

                while(packetWindow.hasPackets()) {
                    if(!waitForResponsePacket()) {
                        for(int i = 0; i < packetWindow.size(); i++) {
                            if(packetWindow.get(i) != null) {
                                System.out.println("[CLIENT]: getting packet from window with SeqNO: " + PacketData.getSeqNo(packetWindow.get(i)));
                                resendPacket(proxy.interfere(packetWindow.get(i)));
                            }
                        }
                    }
                }
        }
        //Send the End of File packet, signalling the end of the stream
        sendEOFPacket();

    }

    private void createSocket(int port,int timeOut) {
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(timeOut);
        } catch ( SocketException x ) {
            x.printStackTrace();
        }
    }
    private void delayForSimulation(int duration) {
        try {
            Thread.sleep(duration);
        } catch ( InterruptedException x ) {
            x.printStackTrace();
        }
    }
    public int totalPackets() {
        return packetGenerator.packetsLeft() + 1;
    }
    private void sendPacket(DatagramPacket packetToSend) {

        System.out.println("[CLIENT]: [SENDING]: " + PacketData.getSeqNo(sendPacket) + "/" + totalPackets);
        System.out.println("[CLIENT]: PACKET_OFFSET: " + startOffset + " - END: "
                                                   + (startOffset += sendPacket.getLength())
                                                   + "\n");
        System.out.println("[CLIENT]:  packet ACK: " + PacketData.getAckNo(sendPacket));
        System.out.println("[CLIENT]:  packet Len: " + PacketData.getLen(sendPacket));
        System.out.println("PACKET SENT TO PORT: " + sendPacket.getPort());
        System.out.println("PACKET SENT TO Address: " + sendPacket.getAddress());

        try {
            socket.send(packetToSend);
        }catch(IOException io) {
            System.err.println("[CLIENT]: Error in sending packet");
        }
    }
    private boolean waitForResponsePacket() {
        System.out.println("[CLIENT]: ....waiting for response packet...");
        try {
                responsePacket = packetGenerator.getResponsePacket(Packet.ACKPACKETHEADERSIZE);
                socket.receive(responsePacket);
                packetWindow.remove(responsePacket);
                return true;
        } catch ( IOException x ) {
            return false;
        }
    }
    private void resendPacket(DatagramPacket p) {
        System.out.println("[CLIENT]: [RESENDING] : " + PacketData.getSeqNo(p) + " /" + totalPackets);
        try {
            socket.send(p);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void sendEOFPacket() {
        sendPacket = packetGenerator.getEoFPacket();
        sendPacket(sendPacket);
        packetWindow.add(sendPacket);
        while(!waitForResponsePacket()) {
            resendPacket(sendPacket);
        }
        System.out.print("[CLIENT]: SENT EOF PACKET WITH SEQNO: " + PacketData.getSeqNo(sendPacket));
    }
}
