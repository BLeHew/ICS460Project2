package network;
import generators.*;
import helpers.*;
import network.*;

public class Packet {

    public static final int PACKETHEADERSIZE = 12;
	private short ckSum;
    private short len;
    private int ackNo;
    private int seqNo;
    private byte data[];

    public Packet(int len) {
    		this.len = (byte)len;
    }
    public Packet() {}; // empty constructor

    public byte[] generateHeaderAsArrayOfBytes() {
    	HeaderGenerator hg = new HeaderGenerator(this);
    	return hg.getHeader();
    }
    /**
     * combines the data and header byte[]s and returns them as one byte[]
     * @return
     */
    public byte[] getPacketAsArrayOfBytes(){
	    	return Converter.toBytes(this);
    }
    /*
     * Functions to convert the argument to a byte array.
     * Source: https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java.%20%20%20%20%20%20
     */


    public void setPacketSize(int size) {
        data = new byte[size];
    }

	public short getCksum() {
		return ckSum;
	}

	public void setCksum(byte cksum) {
		this.ckSum = cksum;
	}

	public void setLen(byte len) {
		this.len = len;
	}

	public int getAckno() {
		return ackNo;
	}

	public void setAckno(byte ackno) {
		this.ackNo = ackno;
	}

	public int getSeqno() {
		return seqNo;
	}

	public void setSeqno(byte seqno) {
		this.seqNo = seqno;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getHeaderSize() {
		return generateHeaderAsArrayOfBytes().length;
	}
    public int getLen() {
        return len;
    }


}
