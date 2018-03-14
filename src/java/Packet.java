import java.util.Arrays;

public class Packet {
	private byte cksum;
    private byte len;
    private byte ackno;
    private byte seqno;
    private byte data[];

    public Packet(byte len) {
    		this.len = len;
    }
    
    private byte[] generateHeaderAsArrayOfBytes() {
    		byte header[] = null;
    		Arrays.fill(header, cksum);
    		Arrays.fill(header, len);
    		Arrays.fill(header, ackno);
    		Arrays.fill(header, seqno);
		return header;
    }
    
    /**
     * combines the data and header byte[]s and returns them as one byte[]
     * @return
     */
    public byte[] getPacketAsArrayOfBytes(){
	    	byte[] header = generateHeaderAsArrayOfBytes();	    	
	    	byte[] combined = new byte[header.length + data.length];
	    	System.arraycopy(header,0,combined,0         ,header.length);
	    	System.arraycopy(data,0,combined,header.length,data.length);
	    	return combined;
    }

    public void setPacketSize(int size) {
        data = new byte[size];
    }

	public byte getCksum() {
		return cksum;
	}

	public void setCksum(byte cksum) {
		this.cksum = cksum;
	}

	public byte getLen() {
		return len;
	}

	public void setLen(byte len) {
		this.len = len;
	}

	public byte getAckno() {
		return ackno;
	}

	public void setAckno(byte ackno) {
		this.ackno = ackno;
	}

	public byte getSeqno() {
		return seqno;
	}

	public void setSeqno(byte seqno) {
		this.seqno = seqno;
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

}
