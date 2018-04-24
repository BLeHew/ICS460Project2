package packet;
import helpers.*;

public class Packet {


    public static final short CHECKSUMGOOD = 0;
    public static final short CHECKSUMBAD = 1;

    public static final short DATAHEADERSIZE = 12;
    public static final short ACKPACKETHEADERSIZE = 8;
	private short ckSum;
    private short len = 0;
    private final int ackNo;
    private final int seqNo;
    private final byte data[];

    //private Header header;
    //regular data packet
    public Packet(short len,
                  int ackNo,
                  int seqNo,
                  byte[] data) {
        if(len != 0) {
            this.len = (short)(DATAHEADERSIZE + len);
        }
        this.ackNo = ackNo;
        this.seqNo = seqNo;
        this.data = data;
        setCkSum();
    }
    //acknowledgement packet
    public Packet(short len,int ackNo) {
        this.len = len;
        this.ackNo = ackNo;
        this.seqNo = 0;
        this.data = null;
        setCkSum();
    }
    public byte[] generateHeaderAsArrayOfBytes() {
    	return new Header(this).getData();
    }
    private void setCkSum() {
        ckSum = CheckSumTools.getChkSum(get());
    }
    /**
     * combines the data and header byte[]s and returns them as one byte[]
     * @return
     */
    public byte[] get(){
	   return Converter.toBytes(this);
    }
	public short getCkSum() {
		return ckSum;
	}

	public int getAckno() {
		return ackNo;
	}

	public int getSeqno() {
		return seqNo;
	}

	public byte[] getData() {
		return data;
	}

    public short getLen() {
        return len;
    }



}
