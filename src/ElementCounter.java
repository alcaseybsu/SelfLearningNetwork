//Mason 2/14/2024
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ElementCounter {

    public static int countCapitalLinesBetweenHeaders(String filePath, String startHeader, String endHeader) {
        int elementCount = 0;
        boolean withinHeaders = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) { // If the line isn't empty
                if (line.equals(startHeader)) { // If we are in the search area within the config file
                    withinHeaders = true;
                } else if (line.equals(endHeader)) { // If we leave the search area within the config file
                    withinHeaders = false;
                    // If we are within the search area, the line isn't empty, and There's a computer name in the first element
                } else if (withinHeaders && !line.isEmpty() && Character.isUpperCase(line.charAt(0))) {
                    elementCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return elementCount;
    }


    public static int generateNumber(String startHeader, String endHeader){

        String filePath = "src/resources/config.txt";

        int result = countCapitalLinesBetweenHeaders(filePath, startHeader, endHeader);

        return result;
    }
}
