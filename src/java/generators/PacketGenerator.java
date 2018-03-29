package generators;

import java.io.*;
import java.net.*;

import packet.*;

public class PacketGenerator {
    private FileInputStream fileStreamIn;
    private HeaderGenerator headerGen;
    private byte[] buffer;
    private int packetSize;

    private int seqNo = 1;
    private int ackNo = 1;

    private Packet currentPacket;

    public PacketGenerator(FileInputStream fis, int packetSize) {
        this.packetSize = packetSize;
        fileStreamIn = fis;
        buffer = new byte[packetSize];
        currentPacket = new Packet();
    }
    public PacketGenerator(int packetSize) {
        this.packetSize = packetSize;
        buffer = new byte[packetSize];
    }

    public DatagramPacket getPacketToSend() {

        readFileStreamIntoBuffer();

        //TODO set the acknowledgement number as well as sequence and checksum numbers

        currentPacket.setData(buffer);
        currentPacket.setAckno(ackNo);
        currentPacket.setSeqno(seqNo);
        currentPacket.setCksum((byte) 0);
        return new DatagramPacket(currentPacket.getPacketAsArrayOfBytes(),packetSize);
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
