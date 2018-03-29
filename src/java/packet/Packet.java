package packet;
import helpers.*;

public class Packet {


    public static final short CHECKSUMGOOD = 0;
    public static final short CHECKSUMBAD = 1;

    public static final int PACKETHEADERSIZE = 12;
	private short ckSum;
    private short len;
    private int ackNo;
    private int seqNo;
    private byte data[];

    private PacketHeader packetHeader;

    //Two types of packets, this one is acknowledgement packet
    public Packet(int ackNo) {
    		this.ackNo = ackNo % 64;
    		this.len = 8;
    }
    //this is a data packet
    public Packet(int ackNo, int seqNo, byte[] data) {
        this.ackNo = ackNo;
        this.seqNo = seqNo;
        this.data = data;

        //data packet length will be the size of the data + 12 bytes for the header
        this.len = (short) (data.length + PACKETHEADERSIZE);
    }
    public Packet() {}; // empty constructor

    public byte[] generateHeaderAsArrayOfBytes() {
    	packetHeader = new PacketHeader(this);
    	return packetHeader.getHeader();
    }
    /**
     * combines the data and header byte[]s and returns them as one byte[]
     * @return
     */
    public byte[] getPacketAsArrayOfBytes(){
	    	return Converter.toBytes(this);
    }
    /**Determines the type of packet based on the length of the packer
     *
     * @return 0 if ackNo packet, 1 if data packet,  and 2 if end of transmission packet
     */
    public int getType() {
        if(len == -1) {
            return 2;
        }
        if(len < 12) {
            return 0;
        }
        else
            return 1;
    }

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

	public void setAckno(int ackNo) {
		this.ackNo = ackNo;
	}

	public int getSeqno() {
		return seqNo;
	}

	public void setSeqno(int seqno) {
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
