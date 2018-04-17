package network;

import org.apache.commons.cli.*;

public class Driver {
    //------------- public map for all nodes on network----
    public static int CLIENTPORT = 9875;
    public static int SERVERPORT = 9876;
    public static String IPADDRESS = "localhost";
    public static int INTERFERENCE_PERCENTAGE = 30;
    public static int PACKET_SIZE = 50;
    public static int WINDOW_SIZE = 1;
    public static int TIMEOUT_INTERVAL = 1000;

    @SuppressWarnings("unused")
    public static void main(String args[]) {
            //  command line flags code
            Options options = new Options();
            options.addOption("boot", true, "boot client or server");
            options.addOption("s", true, "packet size");
            options.addOption("t", true, "timeout interval");
            options.addOption("w", true, "window size");
            options.addOption("d", true, "percentage of datagrams to corrupt, delay, or drop");
            options.addOption("ip", true, "ip address");
            options.addOption("port", true, "port");

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (cmd.hasOption("s")) {
            PACKET_SIZE = Integer.parseInt(cmd.getOptionValue("s"));
        }

        if (cmd.hasOption("t")) {
            TIMEOUT_INTERVAL = Integer.parseInt(cmd.getOptionValue("t"));
        }

        if (cmd.hasOption("w")) {
            WINDOW_SIZE = Integer.parseInt(cmd.getOptionValue("w"));
        }

        if (cmd.hasOption("d")) {
            INTERFERENCE_PERCENTAGE = (int) (Double.parseDouble(cmd.getOptionValue("d")) * 100);
        }

        if (cmd.hasOption("ip")) {
            IPADDRESS = cmd.getOptionValue("ip");
        }

            //  end of command line flags code

        /* Client Constructor parameters:
         * int packetSize -- the size of each packet sent, 1-500.
         * int timeOut -- the duration before the client socket times out, in milliseconds
         * int windowSize -- the size of the window that the packets are being placed in, suggest - 5
         * int port -- the port that this socket is assigned to
         * int interference -- the percentage of packets that will be dropped or corrupted
         * int delay -- the delay time in milliseconds of the simulation
         */
        /*
         * Server Constructor parameters:
         * int windowSize -- see client
         * int interference -- see client
         * InetAddress iPAddress -- the iPAddress to set this server to.
         * int port -- see client
        */
        if (cmd.hasOption("boot")) {
            if (cmd.getOptionValue("boot").equals("client")) {
                if (cmd.hasOption("port")) {
                       CLIENTPORT = Integer.parseInt(cmd.getOptionValue("port"));
                }
                   Client client = new Client(PACKET_SIZE, TIMEOUT_INTERVAL, WINDOW_SIZE,Driver.CLIENTPORT, INTERFERENCE_PERCENTAGE, 1000);
            }else if (cmd.getOptionValue("boot").equals("server")) {
                if (cmd.hasOption("port")) {
                       SERVERPORT = Integer.parseInt(cmd.getOptionValue("port"));
                }
                   Server server = new Server(WINDOW_SIZE ,INTERFERENCE_PERCENTAGE ,IPADDRESS,Driver.SERVERPORT);
            }
        }
        else {
          Server server = new Server(WINDOW_SIZE ,INTERFERENCE_PERCENTAGE ,IPADDRESS,Driver.SERVERPORT);
          Client client = new Client(PACKET_SIZE, TIMEOUT_INTERVAL, WINDOW_SIZE,Driver.CLIENTPORT, INTERFERENCE_PERCENTAGE, 1000);
            }
    }

}

