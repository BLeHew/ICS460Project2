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

        DatagramPacket copy = new DatagramPacket(p.getData(),p.getLength(),p.getAddress(),p.getPort());

        if(packets[index] == null) {
            packets[index] = copy;
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
        return numPackets > 0;
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
        return numPackets < 1;
    }

    public void print() {
        System.out.print("PacketWindow packets : ");
        for(DatagramPacket p : packets) {
            if(p != null) {
                System.out.print("len: " + PacketData.getLen(p));
                System.out.print(" seqNo: " + PacketData.getSeqNo(p) + "\t");
            }
        }
        System.out.print("\n");
    }

}
