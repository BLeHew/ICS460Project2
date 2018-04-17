package packet;
import helpers.*;

public class PacketHeader {
    private final byte[] data;
    private final Packet packet;

    //Generates a header using the given packet
    public PacketHeader(Packet packet) {
        data = new byte[Packet.DATAHEADERSIZE];
        this.packet = packet;


        addLen();
        addAckNo();
        addCheckSum();

        if(packet.getData() != null) {
            addSeqNo();
        }

    }
    public byte[] getData() {
        return data;
    }
    private void addCheckSum() {
        data[0] = 0;
        data[1] = 0;
    }
    private void addLen() {
        byte[] length = Converter.toBytes(packet.getLen());
        data[2] = length[0];
        data[3] = length[1];
    }
    private void addAckNo() {
        byte[] acknowledgementNumber = Converter.toBytes(packet.getAckno());
        data[4] = acknowledgementNumber[0];
        data[5] = acknowledgementNumber[1];
        data[6] = acknowledgementNumber[2];
        data[7] = acknowledgementNumber[3];
    }
    private void addSeqNo() {
        byte[] sequenceNumber = Converter.toBytes(packet.getSeqno());
        data[8] = sequenceNumber[0];
        data[9] = sequenceNumber[1];
        data[10] = sequenceNumber[2];
        data[11] = sequenceNumber[3];
    }

}
