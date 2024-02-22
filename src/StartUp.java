//Mason 2/14/24
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class StartUp {

    // Starts the setup by prompting for computer generation
    public static void main(String[] args) {
        ComputerGeneration();
    }
    //------------------------------------------------------------------------------------------------------------

    public static void ComputerGeneration() {
        // Defines area of search (list of computers) in the config file
        String startHeader = "# Computer IP and port numbers";
        String endHeader = "# End Computer List";
        String element = "Computers";

        // Returns the number of computers found in the config file
        int numOfComputers = ElementCounter.generateNumber(startHeader, endHeader);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Number of Computers in configuration file: " + numOfComputers);
            System.out.println("Generate computers with data provided from the file?");
            System.out.println("(y or n)");
            String compResponse = scanner.nextLine();

            if (Objects.equals(compResponse, "y")) {
                //Generates computers with data found in config file
                processLinesBetweenHeaders(element, startHeader, endHeader);
                //Prompt user about Generating Switches
                SwitchGeneration();

            } else if (Objects.equals(compResponse, "n")){
                SwitchGeneration();
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------

    public static void SwitchGeneration(){
        // Defines area of search (list of Switches) in the config file
        String startHeader = "# Different port range for switches";
        String endHeader = "# End Switch List";
        String element = "Switches";

        // Returns number of switches found ing the config file
        int numOfSwitches = ElementCounter.generateNumber(startHeader, endHeader);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Number of Switches in configuration file: " + numOfSwitches);
            System.out.println("Generate Switches with data provided from the file?");
            System.out.println("(y or n)");
            String compResponse = scanner.nextLine();

            if (Objects.equals(compResponse, "y")) {
                processLinesBetweenHeaders(element, startHeader, endHeader);
            } else if (Objects.equals(compResponse, "n")){

            } else {

            }
        }
    }

    //------------------------------------------------------------------------------------------------------------

        public static void processLinesBetweenHeaders(String element, String startHeader, String endHeader) {

            String filePath = "src/resources/config.txt";
            boolean withinHeaders = false;

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.equals(startHeader)) {
                        withinHeaders = true;
                    } else if (line.equals(endHeader)) {
                        withinHeaders = false;
                    } else if (withinHeaders && !line.isEmpty()) {
                        // Split the line by commas
                        String[] parts = line.split(",");

                        // Check if there are three parts
                        if (parts.length == 3) {
                            if(element == "Computers") {
                                GenerateComputer(parts[0].trim(), parts[1].trim(), Integer.parseInt(parts[2].trim()));
                            }
                            // Format used in Switch Class
                            else if(element == "Switches"){
                                Map<String, String> neighbors = Parser.parseConfig(filePath,parts[0].trim());
                                GenerateSwitch(parts[0].trim(), Integer.parseInt(parts[2].trim()), neighbors);
                            }
                        } else {
                            // Handle invalid lines
                            System.out.println("Invalid line: " + line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    //------------------------------------------------------------------------------------------------------------

    public static void GenerateComputer(String name, String IpAddress, int Port) {
        try {
            Computer computer = new Computer(name, IpAddress, Port);
            computer.start();
        } catch (UnknownHostException e) {
            System.err.println("Computer setup error: " + e.getMessage());
        }
    }

    //------------------------------------------------------------------------------------------------------------

    public static void GenerateSwitch(String name, int portNum, Map<String, String> neighbors) {
        try {
            Switch mySwitch = new Switch(name, portNum, neighbors);
            Thread switchThread = new Thread(mySwitch);
            switchThread.start();

        } catch (UnknownHostException e) {
            System.err.println("Computer setup error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}





