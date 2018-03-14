
public class Packet {
    private short cksum;
    private short len;
    private int ackno;
    private int seqno;
    private byte data[];

    public Packet() {};

    public void setPacketSize(int size) {
        data = new byte[size];
    }
}
