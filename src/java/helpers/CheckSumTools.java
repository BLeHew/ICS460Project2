package helpers;

import java.net.*;
import java.util.zip.*;

import packet.*;

public class CheckSumTools {

    public static short getChkSum(DatagramPacket p) {
        Checksum checksum = new CRC32();

        byte[] temp = new byte[p.getLength() - 2];

        System.arraycopy(p.getData(), 2, temp, 0, p.getLength() - 2);

        checksum.update(temp, 0,temp.length);

        long checkSumValue = checksum.getValue();

        return (short)(checkSumValue);
    }
    public static byte[] getChkSumInBytes(byte[] b) {
        Checksum checksum = new CRC32();

        byte[] temp = new byte[b.length - 2];

        System.arraycopy(b, 2, temp, 0, b.length - 2);

        checksum.update(temp, 0,temp.length);

        long checkSumValue = checksum.getValue();

        return Converter.toBytes((short) checkSumValue);
    }
    public static boolean testChkSum(DatagramPacket p) {

        return PacketData.getCkSum(p) == getChkSum(p);
    }
}
