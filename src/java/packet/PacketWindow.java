package packet;

import java.net.*;
import java.util.*;

import helpers.*;

public class PacketWindow {
    private ArrayList<DatagramPacket> packets = new ArrayList<>();
    private int size;
    //private boolean full = false;

    public PacketWindow(int size) {
        this.size = size;
    }

    public void add(DatagramPacket p) {
        if(packets.size() >= size) {
            System.out.println("Window is full");
            return;
        }
        packets.add(p);
    }
    public boolean isFull() {
        return packets.size() == size;
    }
    public void remove(DatagramPacket p) {
        int otherAckno = Converter.getAckNo(p);
        for(int i = 0; i < packets.size(); i++) {
            if(Converter.getAckNo(packets.get(i)) == otherAckno){
                packets.remove(i);
            }
        }
    }
}
