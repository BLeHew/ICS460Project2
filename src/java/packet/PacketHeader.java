package packet;
import helpers.*;

public class PacketHeader {
    private byte[] data;
    private Packet packet;


    //Generates a header using the given packet
    public PacketHeader(Packet packet) {
        data = new byte[Packet.PACKETHEADERSIZE];
        this.packet = packet;

        addCheckSum();
        addLen();
        addAckNo();

        if(packet.getData() != null) {
            addSeqNo();
        }

    }
    public byte[] getHeader() {
        return data;
    }
    private void addCheckSum() {
        byte[] checkSum = Converter.toBytes(packet.getCksum());
        data[0] = checkSum[0];
        data[1] = checkSum[1];
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
