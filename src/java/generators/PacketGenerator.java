package generators;

import java.io.*;
import java.net.*;

import packet.*;

public class PacketGenerator {
    private final FileInputStream fileStreamIn;
    private final InetAddress ipAddress;
    private final int port;

    private byte[] buffer;
    private int dataLength;

    private int seqNo = 1;
    private int ackNo = 1;

    private int fileSize;

    public PacketGenerator(FileInputStream fis, int packetSize, InetAddress iPAddress, int port) {
        this.dataLength = packetSize;
        fileStreamIn = fis;
        buffer = new byte[packetSize];
        this.ipAddress = iPAddress;
        this.port = port;
    }
    public PacketGenerator(int packetSize) {
        this.dataLength = packetSize;
        buffer = new byte[packetSize];
        this.ipAddress = null;
        this.port = -1;
        this.fileStreamIn = null;
    }

    public DatagramPacket getPacketToSend() {

        if(dataLeft() < dataLength) {
            buffer = new byte[dataLeft()];
            dataLength = dataLeft();
        }
        readFileStreamIntoBuffer();

        ackNo += dataLength + Packet.DATAHEADERSIZE;


        Packet packet = new Packet(Packet.CHECKSUMGOOD, (short) (dataLength), ackNo, seqNo, buffer);

        seqNo += 1;

        byte[] temp = packet.getPacketAsArrayOfBytes();

        return new DatagramPacket(temp, temp.length, ipAddress, port);

    }
    public int nextPacketSize() {
        return dataLength;
    }
    public DatagramPacket getEoFPacket() {
        seqNo++;

        Packet p = new Packet(Packet.CHECKSUMGOOD,(short)0,ackNo,seqNo,new byte[0]);

        byte[] temp = p.getPacketAsArrayOfBytes();



        return new DatagramPacket(temp,temp.length,ipAddress,port);
    }
    public DatagramPacket getResponsePacket(int size) {
        return new DatagramPacket(new byte[size],size);
    }
    public DatagramPacket getAckPacket(DatagramPacket p) {
        Packet packet = new Packet(Packet.CHECKSUMGOOD,(short) 0, PacketData.getAckNo(p));

        return new DatagramPacket(packet.getPacketAsArrayOfBytes(), Packet.ACKPACKETHEADERSIZE,p.getAddress(),p.getPort());
    }
    /**
     *
     * @return true if the FileStream has more data in it to send, false if not
     */
    public boolean hasMoreData() {
        boolean value  = false;
        try {
            value = fileStreamIn.available() != 0;
        } catch ( IOException x ) {
            x.printStackTrace();
        }
        return value;
    }
    public int dataLeft() {
        try {
            return fileStreamIn.available();
        } catch ( IOException x ) {
            return 0;
        }
    }
    public int packetsLeft() {
        int numPacketsLeft = 0;
        try {
            numPacketsLeft = fileStreamIn.available()/dataLength;
        } catch ( IOException x ) {
            x.printStackTrace();
        }
        return numPacketsLeft;
    }
    private void readFileStreamIntoBuffer() {
        //Using 0 for the offset, since were already creating the header in the packet class

        try {
            fileStreamIn.read(buffer,0, dataLength);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
}
