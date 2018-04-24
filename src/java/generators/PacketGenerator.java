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

    //Copy constructor
    public PacketGenerator(PacketGenerator other) {
        this.dataLength = other.dataLength;
        this.fileStreamIn = other.fileStreamIn;
        this.ipAddress = other.ipAddress;
        this.port = other.port;
        this.buffer = other.buffer;
    }

    public PacketGenerator(FileInputStream fis, int dataLength, InetAddress iPAddress, int port) {
        this.dataLength = dataLength;
        fileStreamIn = fis;
        buffer = new byte[dataLength];
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

    public DatagramPacket getDataPacket() {

        if(dataLeft() < dataLength) {
            buffer = new byte[dataLeft()];
            dataLength = dataLeft();
        }
        readFileStreamIntoBuffer();

        ackNo += dataLength + Packet.DATAHEADERSIZE;

        Packet p = new Packet((short) (dataLength), ackNo, seqNo, buffer);

        seqNo += 1;

        return new DatagramPacket(p.get(), dataLength + Packet.DATAHEADERSIZE ,ipAddress,port);
    }
    public DatagramPacket getEoFPacket() {

        ackNo += 1000;

        Packet p = new Packet((short)0,ackNo,seqNo,new byte[0]);
        return new DatagramPacket(p.get(),p.get().length,ipAddress,port);
    }
    public DatagramPacket getResponsePacket(int size) {
        return new DatagramPacket(new byte[size],size);
    }
    public DatagramPacket getAckPacket(DatagramPacket p) {

        Packet packet = new Packet((short) 0, Data.getAckNo(p));

        return new DatagramPacket(packet.get(), packet.get().length,p.getAddress(),p.getPort());
    }
    /**
     *
     * @return true if the FileStream has more data in it to send, false if not
     */
    public boolean hasMoreData() {
        try {
            return fileStreamIn.available() != 0;
        } catch ( IOException x ) {
            return false;
        }
    }
    public int dataLeft() {
        try {
            return fileStreamIn.available();
        } catch ( IOException x ) {
            return 0;
        }
    }
    public int packetsLeft() {
        try {
            return fileStreamIn.available()/dataLength;
        } catch ( IOException x ) {
            return 0;
        }
    }
    private void readFileStreamIntoBuffer() {
        try {
            fileStreamIn.read(buffer,0, dataLength);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
}
