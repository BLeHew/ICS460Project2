package packet;

import java.net.*;
import java.util.*;

public class PacketWindow {
    private ArrayList<DatagramPacket> packets = new ArrayList<>();
    private int size;
    private boolean full = false;

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
            full = true;
        }
    }
    public boolean isFull() {
        return full;
    }
    public void remove(DatagramPacket p) {
        int otherAckno = Packet.getAckNo(p);
        for(int i = 0; i < packets.size(); i++) {
            if(Packet.getAckNo(packets.get(i)) == otherAckno){
                packets.remove(i);

                if(packets.isEmpty()) {
                    full = false;
                }
            }
        }
    }
}
