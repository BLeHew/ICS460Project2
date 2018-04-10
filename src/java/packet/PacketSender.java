package packet;

import java.io.*;
import java.net.*;

import generators.*;
import helpers.*;
import network.Proxy;

public class PacketSender{
    private final PacketGenerator gen;
    private final Proxy proxy;
    private final PacketWindow packetWindow;
    private DatagramSocket socket;
    private final int delayTime;

    private final int totalPackets;
    private int startOffset = 0;

    public PacketSender(PacketGenerator packetGenerator,int delayTime,int interference, int windowSize,
                        int port,int timeOut) {
        this.gen = new PacketGenerator(packetGenerator);
        this.proxy =  new Proxy(interference);
        this.packetWindow = new PacketWindow(windowSize);
        totalPackets = packetGenerator.packetsLeft() + 1;
        this.delayTime = delayTime;
        createSocket(port,timeOut);
    }
    public void start() {
        while(gen.hasMoreData()) {
                 //keep sending packet while the packetwindow isn't full and the packetgenerator has more packets to send.
                 while(!packetWindow.isFull() && gen.hasMoreData()) {

                    sendPacket(gen.getPacketToSend());
                    delayForSimulation(delayTime);
                 }

                while(packetWindow.hasPackets()) {
                    if(!waitForResponsePacket(gen.getResponsePacket(Packet.ACKPACKETHEADERSIZE))) {
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
        sendEOFPacket(gen.getEoFPacket());

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
        return gen.packetsLeft() + 1;
    }
    private void sendPacket(DatagramPacket p) {

        System.out.println("[CLIENT]: [SENDING]: " + PacketData.getSeqNo(p) + "/" + totalPackets);
        System.out.println("[CLIENT]: PACKET_OFFSET: " + startOffset + " - END: "
                                                   + (startOffset += p.getLength())
                                                   + "\n");
        System.out.println("[CLIENT]:  packet ACK: " + PacketData.getAckNo(p));
        System.out.println("[CLIENT]:  packet Len: " + PacketData.getLen(p));

        try {
            socket.send(proxy.interfere(p));
        }catch(IOException io) {
            System.err.println("[CLIENT]: Error in sending packet");
        }
        packetWindow.add(p);
    }
    private boolean waitForResponsePacket(DatagramPacket p) {
        System.out.println("\n[CLIENT]: ....waiting for response packet...");

        try {
            socket.receive(p);
        } catch ( IOException x ) {
            return false;
        }
        if(CheckSumTools.testChkSum(p)) {
            packetWindow.remove(p);
            return true;
        }
        else
            return false;
    }
    private void resendPacket(DatagramPacket p) {
        System.out.println("[CLIENT]: [RESENDING] : " + PacketData.getSeqNo(p) + " /" + totalPackets);
        try {
            socket.send(p);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    private void sendEOFPacket(DatagramPacket p) {
        sendPacket(p);
        packetWindow.add(p);
        System.out.print("[CLIENT]: SENT EOF PACKET WITH ACKNO: " + PacketData.getAckNo(p));
        while(!waitForResponsePacket(p)) {
            resendPacket(p);
        }

    }
}
