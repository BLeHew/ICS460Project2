package network;

import java.net.*;
import java.util.*;

import packet.*;


public class Proxy {

    private int errorPercent;

    private Random r;

    public Proxy(int errorPercent) {
        this.errorPercent = errorPercent;
        r = new Random();
        r.setSeed(6000);
    }

    public DatagramPacket interfere(DatagramPacket packet) {

        byte[] temp = Arrays.copyOf(packet.getData(), packet.getLength());

        DatagramPacket copy = new DatagramPacket(temp,temp.length,packet.getAddress(),packet.getPort());

        int i = r.nextInt(4) + 1;
        int j = r.nextInt(100);

        if(j < errorPercent) {
            switch(i) {
                case 1: System.out.println("[PROXY]: !!Byte changed in packet number: " + PacketData.getSeqNo(packet) + " !!");
                    return changeByteInPacket(copy);
                case 2: System.out.println("[PROXY]: !!Check sum altered in packet number: " + PacketData.getSeqNo(packet) + " !!");
                    return changeChecksumToBad(copy);
                case 3: System.out.println("[PROXY]: !!Byte dropped in packet number: " + PacketData.getSeqNo(packet) + " !!");
                    return dropByteFromPacket(copy);
                case 4: System.out.println("[PROXY]: !!Packet dropped!  number: " + PacketData.getSeqNo(packet) + " !!");
                    return makePacketDisappear(copy);
            }
        }
        return copy;
    }

    //create random int (0 --> percent)


    //simulate byte value change in packet
    private DatagramPacket changeByteInPacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        data[13]=(byte)(r.nextInt(1));
        packet.setData(data);
        return packet;
    }
    //simulate the checksum being messed up
    private DatagramPacket changeChecksumToBad(DatagramPacket packet) {
        PacketData.setCkSumBad(packet); //TODO actually use checksums.
        return packet;
    }

    //Simulate missing byte in packet
    private DatagramPacket dropByteFromPacket(DatagramPacket packet) {
        byte[] missingData = new byte[packet.getData().length - 1];
        packet.setData(missingData);
        return packet;
    }

    ///Simulate a dropped packet.
    private DatagramPacket makePacketDisappear(DatagramPacket packet){
        packet.setPort(Driver.SERVERPORT+10);
        return packet;
    }

}