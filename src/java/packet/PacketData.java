package packet;

import java.net.*;

import helpers.*;

public class PacketData {
    //helper method to get the acknowledgement number from the given data packet
    public static int getSeqNo(DatagramPacket p) {
        byte[] temp = new byte[4];

        int j = 8;
        for(int i = 0; i < temp.length ; i++) {
            temp[i] = p.getData()[j];
            j++;
        }

        return Converter.toInt(temp);

    }
  //helper method to get the sequence number from the given data packet
    public static short getLen(DatagramPacket p) {
        byte[] temp = new byte[2];

        temp[0] = p.getData()[2];
        temp[1] = p.getData()[3];


        return (short)Converter.toInt(temp);

    }
    //helper method to get the sequence number from the given data packet
    public static int getAckNo(DatagramPacket p) {
        byte[] temp = new byte[4];

        temp[0] = p.getData()[4];
        temp[1] = p.getData()[5];
        temp[2] = p.getData()[6];
        temp[3] = p.getData()[7];

        return Converter.toInt(temp);

    }
  //helper method to get the ckSum number from the given data packet
    public static short getCkSum(DatagramPacket p) {
        byte[] temp = new byte[2];

        temp[0] = p.getData()[0];
        temp[1] = p.getData()[1];

        return Converter.toShort(temp);
    }
    //helper method to set the ckSum of the packet to a bad state;
    public static void setCkSumBad(DatagramPacket p) {
        p.getData()[0] = 0;
        p.getData()[1] = 1;
    }
  //helper method to set the ckSum of the packet to a good state;
    public static void setCkSumGood(DatagramPacket p) {
        p.getData()[0] = 0;
        p.getData()[1] = 0;
    }
}
