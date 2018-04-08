package generators;

import java.io.*;
import java.net.*;

import packet.*;

public class PacketGenerator {
    private FileInputStream fileStreamIn;
    private byte[] buffer;
    private int packetSize;

    private int seqNo = 1;
    private int ackNo = 1;

    private Packet packet;
    private int fileSize;

    private InetAddress ipAddress;
    private int serverPort;

    public PacketGenerator(FileInputStream fis, int packetSize, InetAddress iPAddress, int serverPort) {
        this.packetSize = packetSize;
        fileStreamIn = fis;
        buffer = new byte[packetSize];
        packet = new Packet();
        this.ipAddress = iPAddress;
        this.serverPort = serverPort;
    }
    public PacketGenerator(int packetSize) {
        this.packetSize = packetSize;
        buffer = new byte[packetSize];
        packet = new Packet();
    }

    public DatagramPacket getPacketToSend() {

        if(dataLeft() < packetSize) {
            buffer = new byte[dataLeft()];
            packetSize = dataLeft();
        }
        readFileStreamIntoBuffer();

        ackNo += packetSize + Packet.DATAHEADERSIZE;

        packet.setSeqno(seqNo);
        seqNo += 1;
        packet.setAckno(ackNo);

        packet.setCksum(Packet.CHECKSUMGOOD);

        packet.setData(buffer);

        byte[] temp = packet.getPacketAsArrayOfBytes();


        DatagramPacket p = new DatagramPacket(temp, temp.length, ipAddress, serverPort);

        return  p;

    }
    public int nextPacketSize() {
        return packetSize;
    }
    public DatagramPacket getEoFPacket() {
        seqNo++;
        packet.setSeqno(seqNo);
        packet.setLen((short) 9999);

        byte[] temp = packet.getPacketAsArrayOfBytes();



        return new DatagramPacket(temp,temp.length,ipAddress,serverPort);
    }
    public DatagramPacket getInitialPacket() {
        return new DatagramPacket(new byte[packetSize],packetSize);
    }
    public DatagramPacket getResponsePacket(int size) {
        return new DatagramPacket(new byte[size],size);
    }
    public DatagramPacket getAckPacket(DatagramPacket p) {

        packet.setCksum(PacketData.getCkSum(p));
        packet.setAckno(PacketData.getAckNo(p));

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
            numPacketsLeft = fileStreamIn.available()/packetSize;
        } catch ( IOException x ) {
            x.printStackTrace();
        }
        return numPacketsLeft;
    }
    private void readFileStreamIntoBuffer() {
        //Using 0 for the offset, since were already creating the header in the packet class

        try {
            fileStreamIn.read(buffer,0, packetSize);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
    }
}
