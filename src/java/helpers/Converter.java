package helpers;

import network.*;

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
        byte[] result = new byte[2];

        result[0] = (byte) (s >> 8);
        result[1] = (byte) (s);

        return result;
    }
    public static byte[] toBytes(Packet p) {
        byte[] header = p.generateHeaderAsArrayOfBytes();
        byte[] combined = new byte[header.length + p.getLen()];
        System.arraycopy(header,0,combined,0,header.length);
        System.arraycopy(p.getData(),0,combined,header.length,p.getData().length);
        return combined;
    }
}
