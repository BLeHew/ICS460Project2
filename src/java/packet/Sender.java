package packet;

import java.io.*;
import java.net.*;

import generators.*;
import helpers.*;
import network.Proxy;

public class Sender{
    private final PacketGenerator gen;
    private final Proxy proxy;
    private final Window window;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private final int delayTime;

    private final int totalPackets;
    private int startOffset = 0;

    public Sender(PacketGenerator packetGenerator,int delayTime,int interference, int windowSize,
                        int port,int timeOut) {
        this.gen = new PacketGenerator(packetGenerator);
        this.proxy =  new Proxy(interference);
        this.window = new Window(windowSize);
        totalPackets = packetGenerator.packetsLeft() + 1;
        this.delayTime = delayTime;
        createSocket(port,timeOut);
    }
    public void start() {

        long startTime = System.currentTimeMillis();

        while(gen.hasMoreData()) {
                 //keep sending packet while the packetwindow isn't full and the packetgenerator has more packets to send.
            packet = gen.getDataPacket();

            window.add(packet);

            System.out.print("Sending");
            send(packet, socket);

            // delayForSimulation(delayTime);

            while ( !waitForResponsePacket() ) {
                System.out.print("ReSend. ");
                send(window.get(0), socket);
            }
        }
        //Send the End of File packet, signalling the end of the stream

        System.out.print("Sending ");
        packet = gen.getEoFPacket();

        window.add(packet);

        send(packet,socket);

        long endTime = System.currentTimeMillis() - startTime;

        System.out.print("Took " + endTime/1000 + " seconds to send : " + totalPackets +  " packets");
        while(!waitForResponsePacket()) {
            //System.out.print("ReSend. ");
            send(window.get(0),socket);
        }
        System.out.println("[CLIENT]: Closing socket");

        socket.close();

    }
    private void send(DatagramPacket p,DatagramSocket s) {
        /*
        System.out.println(" " + Data.getSeqNo(p) + " "
            + startOffset + ":" +  (startOffset + Data.getLen(p))
            + " " + System.currentTimeMillis()
            + " " + proxy.send(p, s));
        */
        System.out.println("Sending packet wiht ckSum of : " + Data.getCkSum(p));
        proxy.send(p, s);
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
            System.out.print("[AckRcvd]: " + Data.getSeqNo(packet));
        } catch ( IOException x ) {
            System.out.println("[Timeout]");
            return false;
        }
        if(CheckSumTools.testChkSum(packet)) {
            System.out.println(" [MoveWnd]");
            startOffset += window.remove(packet);
            return true;
        }
        else {
            System.out.println(" [ErrAck].");
        }
        return false;
    }
    }
