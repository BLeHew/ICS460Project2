package packet;

import java.net.*;
import java.util.*;


public class Window {

    private DatagramPacket[] packets;
    private int numPackets;
    private final int size;
    private int dataLength;
    private int eofIndex;

    public Window(int size) {
        this.size = size;
        packets = new DatagramPacket[size];
        dataLength = 0;

    }
    public boolean add(DatagramPacket p) {
        int index = (Data.getSeqNo(p) - 1) % size;

        if(alreadyHas(p)) {
            return false;
        }

        byte[] temp = Arrays.copyOf(p.getData(), p.getLength());

        DatagramPacket copy =
            new DatagramPacket(temp, temp.length, p.getAddress(), p.getPort());

        packets[index] = copy;
        numPackets++;
        dataLength += (Data.getLen(copy) - 12);
        return true;


    }
    public boolean canBeWritten() {

        boolean write = false;


        if(hasMissingPackets()) {
            return write;
        }

        int i = 0;
        while(!write && i < size) {
            if(Data.getLen(packets[i]) == 0) {
                write = true;
            }
            i++;
        }
        return write;
    }
    public int getEoFIndex() {
        return eofIndex;
    }
    public boolean containsEoF() {
        for(int i = 0; i < packets.length;i++) {
            if(packets[i] != null) {
                if(Data.getLen(packets[i]) == 0) {
                    eofIndex = i;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean alreadyHas(DatagramPacket p) {
        for(int i = 0; i < packets.length; i++) {
            if(packets[i] != null) {
                if(Data.getAckNo(p) == Data.getAckNo(packets[i])) {
                    return true;
                }
            }
        }
        return false;
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
        dataLength = 0;
    }
    public int getDataLength() {
        return dataLength;
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
    public int remove(DatagramPacket p) {
        int out = 0;
        int otherAckno = Data.getAckNo(p);
        for(int i = 0; i < packets.length; i++) {
            if(packets[i] != null) {
                if(Data.getAckNo(packets[i]) == otherAckno){
                    out = Data.getLen(packets[i]);
                    packets[i] = null;
                    numPackets--;
                }
            }
        }
        return out;
    }
    private boolean isEmpty() {
        return numPackets < 1;
    }

    public void print() {
        System.out.print("Window packets : ");
        for(DatagramPacket p : packets) {
            if(p != null) {
                System.out.print("len: " + Data.getLen(p));
                System.out.print(" seqNo: " + Data.getSeqNo(p) + "\t");
            }
        }
        System.out.print("\n");
    }
    public int numPackets() {
        return numPackets;
    }
    public void remove(int i) {
        packets[i] = null;
    }

}
