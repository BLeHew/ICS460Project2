package generators;

import java.io.*;
import java.net.*;

import network.*;

public class PacketGenerator {
    private FileInputStream fileStreamIn;
    private ACKGenerator ackGen;
    private byte[] buffer;
    private int packetSize;
    private Packet currentPacket;

    public PacketGenerator(FileInputStream fis, int packetSize) {
        this.packetSize = packetSize;
        fileStreamIn = fis;
        buffer = new byte[packetSize];
        currentPacket = new Packet(packetSize);
    }

    public DatagramPacket getPacketToSend() {
        readFileStreamIntoBuffer();
        //TODO set the acknowledgement number as well as sequence and checksum numbers
        currentPacket.setData(buffer);
        currentPacket.setAckno((byte) 0);
        currentPacket.setCksum((byte) 0);
        return new DatagramPacket(currentPacket.getPacketAsArrayOfBytes(),packetSize);
    }
    /**
     *
     * @return true if the filestream has more data in it to send, false if not
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
