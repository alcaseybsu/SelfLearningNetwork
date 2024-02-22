//Mason & Leah 2/14/2024
import java.io.*;
import java.net.*;
import java.util.*;

public class Computer {

    public static void main(String[] args) {

        GenerateComputer("A", "10.222.120.224", 3000);
        //GenerateComputer("B", "10.222.120.224", 3001);
    }

    private final String name;
    @SuppressWarnings("unused")
    private final InetAddress Address;
    private final int Port;

    public Computer(String name, String IpAddress, int Port) throws UnknownHostException {
        this.name = name;
        this.Address = InetAddress.getByName(IpAddress);
        this.Port = Port;
    }

//------------------------------------------------------------------------------------------------------------
    //Two threads for constant sending & receiving

    public void start() {
        new Thread(this::sendMessages).start();
        new Thread(this::receiveMessages).start();
    }
//------------------------------------------------------------------------------------------------------------

    private void sendMessages() {
        //Defines area of the config file to search. Isolates the file to just the Node connections
        String startHeaderNeighbor = "# Node connections";
        String endHeaderNeighbor = "# End node connections list";

        //Defines area of the config file to search. Searches through all computers and switches
        String startHeaderComputerAndSwitch = "# Computer IP and port numbers";
        String endHeaderComputerAndSwitch = "# End Switch List";

        //Finds its neighbor
        String neighbor = ComputerParser.ComputerNeighborFinder(startHeaderNeighbor, endHeaderNeighbor, name);

        //Gets that neighbors IP and Port data from config file
        String neighborData = ComputerParser.NeighborInfo(startHeaderComputerAndSwitch, endHeaderComputerAndSwitch, neighbor);

        //Separate data into variables
        String[] neighborDataSplit = neighborData.split(",");

        String neighborIP = neighborDataSplit[0];

        System.out.println(neighborIP);

        int neighborPort = Integer.parseInt(neighborDataSplit[1]);
        System.out.println(neighborPort);

        //---------------------------------------------------

        try (DatagramSocket socket = new DatagramSocket()) {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.println("Computer " + name);

                    //System.out.println("Type \"exit\" to end this computers session");
                    System.out.print("Enter The Name Of The Computer You Would Like To Send A Message To: \n");
                    String destName = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(destName)) break;

                    System.out.print("Enter The Message: ");
                    String message = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(message)) break;

                    String payload = name + "|" + destName + "|" + message;
                    byte[] data = payload.getBytes();

                    // Computer A to Computer B test case
                    //---------------------------------------------------
                    InetAddress testIp = InetAddress.getByName("10.222.120.224");
                    int testPort = 3001;
                    DatagramPacket packet = new DatagramPacket(data, data.length, testIp, testPort);
                    //---------------------------------------------------

                    //Computer to switch test case
                    //DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(neighborIP), neighborPort);

                    socket.send(packet);
                    System.out.println("Message Sent!\n\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

//------------------------------------------------------------------------------------------------------------

    private void receiveMessages() {
        try (DatagramSocket socket = new DatagramSocket(Port)) {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                String[] parts = received.split("\\|");
                if (parts.length == 3 && parts[1].equalsIgnoreCase(name)) {
                    System.out.println("Message from " + parts[0] + ": " + parts[2]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
    }
    public static void GenerateComputer(String name, String IpAddress, int Port) {
        try {
            Computer computer = new Computer(name, IpAddress, Port);
            computer.start();
        } catch (UnknownHostException e) {
            System.err.println("Computer setup error: " + e.getMessage());
        }
    }
}

