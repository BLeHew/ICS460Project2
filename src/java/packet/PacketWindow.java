package packet;

import java.net.*;

public class PacketWindow {
    //private ArrayList<DatagramPacket> packets = new ArrayList<>();
    private DatagramPacket[] packets;
    private int size;
    private boolean hasPackets = false;

    public PacketWindow(int size) {
        this.size = size;
        packets = new DatagramPacket[size];

    }
    public void add(DatagramPacket p) {
        System.out.println("Adding packet at window location: " + (PacketData.getSeqNo(p) % size));
        packets[PacketData.getSeqNo(p) % size] = p;
    }
    public int size() {
        return size;
    }
    public boolean hasPackets() {
        int i = 0;
        for(; i < size; i++) {
            if(!(packets[i] == null)) {
                hasPackets = true;
            }
        }
        return hasPackets;
    }

    public DatagramPacket get(int index) {
        return packets[index];
    }
    public void remove(DatagramPacket p) {
        int otherAckno = PacketData.getAckNo(p);
        for(int i = 0; i < packets.length; i++) {
            if(packets[i] != null) {
                if(PacketData.getAckNo(packets[i]) == otherAckno){
                    packets[i] = null;
                    if(isEmpty()) {
                        hasPackets = false;
                    }
                }
            }
        }
    }
    private boolean isEmpty() {
        int i = 0;
        for(; i < size; i++) {
            if(!(packets[i] == null)) {
                return false;
            }
        }
        return true;
    }
    public void print() {
        System.out.print("PacketWindow packets : ");
        for(DatagramPacket p : packets) {
            System.out.print("ackNo: " + PacketData.getAckNo(p));
            System.out.print(" seqNo: " + PacketData.getSeqNo(p) + "\t");
        }
        System.out.print("\n");
    }
}
