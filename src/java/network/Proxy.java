package network;

import java.io.*;
import java.net.*;
import java.util.*;

import packet.*;


public class Proxy {

    private final int errorPercent;

    private Random r;

    public Proxy(int errorPercent) {
        this.errorPercent = errorPercent;
        r = new Random();
        r.setSeed(6000);
    }

    public String interfere(DatagramPacket packet,String str) {
        if(r.nextInt(100) < errorPercent) {
            switch(r.nextInt(4) + 1) {
                case 1: return changeByteInPacket(packet);
                case 2: return changeChecksumToBad(packet);
                case 3: return dropByteFromPacket(packet);
                case 4: return makePacketDisappear(packet);
            }
        }
        return str;
    }
    public String send(DatagramPacket p, DatagramSocket s) {
        byte[] temp = Arrays.copyOf(p.getData(), p.getLength());
        DatagramPacket copy = new DatagramPacket(temp,temp.length,p.getAddress(),p.getPort());

        String str = "SENT";

        str = interfere(copy, str);

        try {
            s.send(copy);
        } catch ( IOException x ) {
            x.printStackTrace();
        }
        return str;
    }
    //create random int (0 --> percent)


    //simulate byte value change in packet
    private String changeByteInPacket(DatagramPacket packet) {
        packet.getData()[6] = (byte)(r.nextInt(10));
        return "ERR";
    }
    //simulate the checksum being messed up
    private String changeChecksumToBad(DatagramPacket packet) {
        Data.setCkSumBad(packet);
        return "CRPT";
    }

    //Simulate missing byte in packet
    private String dropByteFromPacket(DatagramPacket packet) {
        byte[] missingData = Arrays.copyOf(packet.getData(), packet.getLength() - 1);
        packet.setData(missingData);
        return "CRPT";
    }

    ///Simulate a dropped packet.
    private String makePacketDisappear(DatagramPacket packet){
        packet.setPort(Driver.SERVERPORT+10);
        return "DROP";
    }

}