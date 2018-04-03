package packet;

import java.net.*;
import java.util.*;

public class PacketWindow {
    private ArrayList<DatagramPacket> packets = new ArrayList<>();
    private int size;
    private boolean hasPackets = false;

    public PacketWindow(int size) {
        this.size = size;
    }
    public void add(DatagramPacket p) {
        if(packets.size() >= size) {
            System.out.println("Window is full");
            return;
        }
        packets.add(p);
        if(packets.size() == size) {
            hasPackets = true;
        }
    }
    public int size() {
        return packets.size();
    }
    public boolean hasPackets() {
        return hasPackets;
    }
    public DatagramPacket get(int index) {
        return packets.get(index);
    }
    public boolean isEmpty() {
        return packets.isEmpty();
    }
    public void remove(DatagramPacket p) {
        int otherAckno = PacketData.getAckNo(p);
        for(int i = 0; i < packets.size(); i++) {
            if(PacketData.getAckNo(packets.get(i)) == otherAckno){
                packets.remove(i);
                if(packets.isEmpty()) {
                    hasPackets = false;
                }
            }
        }
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
