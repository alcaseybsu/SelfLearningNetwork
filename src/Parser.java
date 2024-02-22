
//Leah & Adrianna 2/14/24
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    // parse configuration based on nodeName
    public static Map<String, String> parseConfig(String configPath, String nodeName) {
        Map<String, String> configDetails = new HashMap<>();

        try {
            File file = new File(configPath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(nodeName)) {
                    // Process the line to extract details
                    String[] parts = line.split(",");
                    for (String part : parts) {
                        String[] keyValue = part.split(":");
                        if (keyValue.length == 2) {
                            configDetails.put(keyValue[0].trim(), keyValue[1].trim());
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return configDetails;
    }

    public static void main(String[] args) {
        // Parse config file and find switch
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src" + File.separator + "resources" + File.separator
                + "config.txt";

        Scanner scanner = new Scanner(System.in);

        // get the instance of Switch
        Switch switchInstance = null;
        try {
            switchInstance = new Switch(filePath, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse each line of config.txt
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String deviceName = parseLine(line);

            // Update the neighbors map in the Switch instance
            Map<String, String> neighbors = parseConfig(filePath, deviceName);
            switchInstance.updateNeighbors(neighbors);
        }

        try {
            File file = new File(filePath);
            scanner = new Scanner(file);

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String parseLine(String line) {
        DeviceNameParser deviceNameParser = new DeviceNameParser();
        IPAddressParser ipAddressParser = new IPAddressParser();
        PortNameParser portNameParser = new PortNameParser();

        String deviceName = deviceNameParser.parse(line);
        String ipAddress = ipAddressParser.parse(line);
        String portName = portNameParser.parse(line);

        System.out.println("Device Name: " + deviceName);
        System.out.println("IP Address: " + ipAddress);
        System.out.println("Port Name: " + portName);
        System.out.println();

        findNeighbor(deviceName);

        return deviceName;
    }

    public static List<String> findNeighbor(String nameDevice) {
        List<String> neighbors = new ArrayList<>();

        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src" + File.separator + "resources" + File.separator
                + "config.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(nameDevice)) {
                    int index = line.indexOf(nameDevice) + nameDevice.length() + 1;
                    String neighbor = line.substring(index);
                    neighbors.add(neighbor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return neighbors;
    }
}

class DeviceNameParser {
    public String parse(String line) {
        return line.split(" ")[0];
    }
}

class IPAddressParser {
    public String parse(String line) {
        int startIndex = line.indexOf('(') + 1;
        int endIndex = line.indexOf(')');
        return line.substring(startIndex, endIndex);
    }
}

class PortNameParser {
    public String parse(String line) {
        String[] parts = line.split(" ");
        return parts[parts.length - 1];
    }
}