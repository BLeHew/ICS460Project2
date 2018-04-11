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
    private DatagramPacket packet;
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
                    packet = gen.getPacketToSend();
                    packetWindow.add(packet);

                    System.out.print("Sending");
                    send(packet,socket);

                    delayForSimulation(delayTime);

                    while(!waitForResponsePacket()){
                        System.out.print("ReSend. ");
                        send(packetWindow.get(0),socket);
                    }
                 }
        }
        //Send the End of File packet, signalling the end of the stream

        System.out.print("Sending ");
        packet = gen.getEoFPacket();
        send(packet,socket);
        packetWindow.add(packet);

        while(!waitForResponsePacket()) {
            System.out.print("ReSend. ");
            send(packetWindow.get(0),socket);
        }
        System.out.println("[CLIENT]: Closing socket");

        socket.close();

    }
    private void send(DatagramPacket p,DatagramSocket s) {
        System.out.println(" " + PacketData.getSeqNo(p) + " "
            + startOffset + ":" +  (startOffset +=PacketData.getLen(p))
            + " " + System.currentTimeMillis()
            + " " + proxy.send(p, s));
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

    private boolean waitForResponsePacket() {
       // System.out.println("[CLIENT]: ....waiting for response packet...");

        try {
            socket.receive(packet);
        } catch ( IOException x ) {
            return false;
        }
        if(CheckSumTools.testChkSum(packet)) {
            packetWindow.remove(packet);
            //System.out.println("[CLIENT]: Response packet received with ACK of: " + PacketData.getAckNo(p) + " removing from window.");
            //System.out.println("[CLIENT]: Number of packets in window: " + packetWindow.numPackets());
            return true;
        }
        return false;
    }
    private void resendPacket(DatagramPacket p) {
       // System.out.println("[CLIENT]: [RESENDING] : " + PacketData.getSeqNo(p) + " /" + totalPackets);
        try {
            socket.send(p);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
    }
