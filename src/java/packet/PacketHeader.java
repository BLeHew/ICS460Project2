package packet;
import helpers.*;

public class PacketHeader {
    private byte[] headerData;
    private Packet packet;


    //Generates a header using the given packet
    public PacketHeader(Packet packet) {
        headerData = new byte[Packet.PACKETHEADERSIZE];
        this.packet = packet;

        addCheckSum();
        addLen();
        addAckNo();

        if(packet.getData() != null) {
            addSeqNo();
        }

    }
    public byte[] getHeader() {
        return headerData;
    }
    private void addCheckSum() {
        byte[] checkSum = Converter.toBytes(packet.getCksum());
        headerData[0] = checkSum[0];
        headerData[1] = checkSum[1];
    }
    private void addLen() {
        byte[] length = Converter.toBytes(packet.getLen());
        headerData[2] = length[0];
        headerData[3] = length[1];
    }
    private void addAckNo() {
        byte[] acknowledgementNumber = Converter.toBytes(packet.getAckno());
        headerData[4] = acknowledgementNumber[0];
        headerData[5] = acknowledgementNumber[1];
        headerData[6] = acknowledgementNumber[2];
        headerData[7] = acknowledgementNumber[3];
    }
    private void addSeqNo() {
        byte[] sequenceNumber = Converter.toBytes(packet.getSeqno());
        headerData[8] = sequenceNumber[0];
        headerData[9] = sequenceNumber[1];
        headerData[10] = sequenceNumber[2];
        headerData[11] = sequenceNumber[3];
    }

}
