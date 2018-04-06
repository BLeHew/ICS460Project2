package packet;

import java.net.*;
import java.util.*;


public class PacketWindow {

    private DatagramPacket[] packets;
    private int numPackets;
    private int size;

    private boolean hasPackets = false;

    public PacketWindow(int size) {
        this.size = size;
        packets = new DatagramPacket[size];

    }
    public void add(DatagramPacket p) {
        int index = (PacketData.getSeqNo(p) - 1) % size;
        //have to make a copy of the packet, or each subsequent packet will reference the first
        byte[] tempData = p.getData();
        int tempLength = p.getLength();

        DatagramPacket copy = new DatagramPacket(tempData,tempLength);

        if(packets[index] == null) {
            packets[index] = copy;

            System.out.println("index: " + index);
            System.out.print("Packets[0]: ");
            System.out.println(packets[0] == null ? "null" : PacketData.getLen(packets[0]) );
            System.out.print("Packets[1]: ");
            System.out.println(packets[1] == null ? "null" : PacketData.getLen(packets[1]) );
            System.out.print("Packets[2]: ");
            System.out.println(packets[2] == null ? "null" : PacketData.getLen(packets[2]) );
            System.out.print("Packets[3]: ");
            System.out.println(packets[3] == null ? "null" : PacketData.getLen(packets[3]) );
            System.out.print("Packets[4]: ");
            System.out.println(packets[4] == null ? "null" : PacketData.getLen(packets[4]) );

            numPackets++;
          }

    }
    public boolean hasMissingPackets() {
        int i = 0;
        for(; i < size; i++) {
            if(packets[i] == null) {
                return true;
            }
        }
        return false;
    }
    public boolean isFull() {
        return numPackets == size;
    }
    public void clear() {
        Arrays.fill(packets,null);
        numPackets = 0;
    }
    public int size() {
        return size;
    }
    public boolean hasPackets() {
        int i = 0;
        for(; i < size; i++) {
            if(!(packets[i] == null)) {
                return true;
            }
        }
        return false;
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
                    numPackets--;
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
            System.out.print("len: " + PacketData.getLen(p));
            System.out.print(" seqNo: " + PacketData.getSeqNo(p) + "\t");
        }
        System.out.print("\n");
    }
}
