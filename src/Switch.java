//Leah & Cyarina 2/14/2024
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Switch implements Runnable {
    private String name;
    @SuppressWarnings("unused")
    private int port;
    private DatagramSocket socket;
    private Map<String, String> neighbors;
    private Map<String, String> forwardingTable;

    public Switch(String name, int port, Map<String, String> neighbors) throws IOException {
        this.name = name;
        this.port = port;
        this.neighbors = neighbors;
        this.forwardingTable = new HashMap<>();
        this.socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String receivedData = new String(packet.getData(), 0, packet.getLength()).trim();
                // format is srcMAC|destMAC|message
                String[] parts = receivedData.split("\\|");
                if (parts.length >= 3) {
                    String srcName = parts[0];
                    String destName = parts[1];
                    @SuppressWarnings("unused")
                    String message = parts[2];

                    System.out.println("[" + name + "] Received frame: " + receivedData);

                    // Update forwarding table based on Ethernet learning algorithm
                    updateForwardingTable(srcName, packet.getAddress().getHostAddress() + ":" + packet.getPort());

                    // Forward the frame based on dest MAC address
                    forwardFrame(destName, receivedData);
                }
            } catch (IOException e) {
                System.out.println("[" + name + "] Error receiving packet: " + e.getMessage());
                break;
            }
        }
    }

    private void updateForwardingTable(String mac, String virtualPort) {
        // Implement the Ethernet learning algorithm
        // Update the forwarding table based on the source MAC address
        synchronized (forwardingTable) {
            if (!forwardingTable.containsKey(mac)) {
                forwardingTable.put(mac, virtualPort);
                System.out.println("[" + name + "] Updated forwarding table: " + mac + " -> " + virtualPort);
            }
        }
    }

    private void forwardFrame(String destName, String data) {
        String destination = forwardingTable.get(destName);
        if (destination != null) {
            try {
                String[] destParts = destination.split(":");
                InetAddress destAddress = InetAddress.getByName(destParts[0]);
                int destPort = Integer.parseInt(destParts[1]);

                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), destAddress, destPort);
                socket.send(packet);
                System.out.println("[" + name + "] Forwarded frame to: " + destName + " at " + destination);
            } catch (IOException e) {
                System.out.println("[" + name + "] Error forwarding frame: " + e.getMessage());
            }
        } else {
            // Flooding logic
            System.out.println("[" + name + "] Destination unknown, flooding to all neighbors.");

            for (String neighbor : neighbors.values()) {
                try {
                    String[] neighborParts = neighbor.split(":");
                    InetAddress neighborAddress = InetAddress.getByName(neighborParts[0]);
                    int neighborPort = Integer.parseInt(neighborParts[1]);

                    DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), neighborAddress,
                            neighborPort);
                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("[" + name + "] Error flooding frame: " + e.getMessage());
                }
            }
        }
    }

    public void updateNeighbors(Map<String, String> newNeighbors) {
        this.neighbors.putAll(newNeighbors);
    }

    public static void main(String[] args) {
        // Main method for starting the Switch
        try {
            String configPath = "src/resources/config.txt";
            String nodeName = "S1";
            Map<String, String> neighbors = Parser.parseConfig(configPath, nodeName);

            Switch mySwitch = new Switch(nodeName, 4000, neighbors);
            System.out.println("S1");
            Thread switchThread = new Thread(mySwitch);
            switchThread.start();
        } catch (IOException e) {
            System.out.println("Error creating Switch: " + e.getMessage());
        }
    }
}
