package helpers;

import java.nio.*;

import packet.*;

public class Converter {
    /*
     * Functions to convert the argument to a byte array.
     * Source: https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java.%20%20%20%20%20%20
     */

    public static byte[] toBytes(int i) {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);

        return result;
    }
    public static byte[] toBytes(short s) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(s).array();

    }
    //convert the packet to its byte representation
    public static byte[] toBytes(Packet p) {
        byte[] header = p.generateHeaderAsArrayOfBytes();

        if(p.getData() == null) {
            return header;
        }
        else {
            byte[] combined = new byte[header.length + p.getData().length];
            System.arraycopy(header,0,combined,0,header.length);
            System.arraycopy(p.getData(),0,combined,header.length,p.getData().length);
            return combined;
        }
    }

    //convert the byte array to an integer.
    //source : https://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
    public static int toInt(byte[] array) {
        return   array[3] & 0xFF |
            (array[2] & 0xFF) << 8 |
            (array[1] & 0xFF) << 16 |
            (array[0] & 0xFF) << 24;
    }
    public static short toShort(byte[] array) {
        return ByteBuffer.wrap(array).getShort();
    }

}
