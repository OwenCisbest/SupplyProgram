import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class App {
    static class Part {
        String partName;
        String partNumber;
        
        Part(String partName, String partNumber) {
            this.partName = partName;
            this.partNumber = partNumber;
        }
    }
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("Enter vehicle VIN:");
        while(running = true){
            if(scanner.nextLine().equals("exit")){
                running = false;
            }
            else{
                String vin = scanner.nextLine();        
                getCarInfo(vin);
            }
        }        
    }
    
    public static void getCarInfo(String vin) throws Exception {
        try {
            // First, decode the VIN to get year, make, model
            String decodeUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/" + vin + "?format=json";
            String vehicleInfo = makeAPICall(decodeUrl);
            System.out.println(vehicleInfo);
            
            // Debug: print all variables to see what's available
            printAvailableVariables(vehicleInfo);
            
        } catch(Exception e) {
            System.out.println("Error calling API: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printAvailableVariables(String json) {
        System.out.println("\n=== Available Variables ===");
        int index = 0;
        while ((index = json.indexOf("\"Variable\":\"", index)) != -1) {
            int varStart = index + 12;
            int varEnd = json.indexOf("\"", varStart);
            if (varEnd == -1) break;
            String variable = json.substring(varStart, varEnd);

            // Search BACKWARDS for Value since it appears before Variable in each object
            int valueIdx = json.lastIndexOf("\"Value\":", index);
            if (valueIdx == -1) {
                index = varEnd + 1;
                continue;
            }

            int afterColon = valueIdx + 8;
            String value;
            if (json.startsWith("null", afterColon)) {
                value = "";
            } else if (json.charAt(afterColon) == '"') {
                int valueStart = afterColon + 1;
                int valueEnd = json.indexOf("\"", valueStart);
                if (valueEnd == -1) break;
                value = json.substring(valueStart, valueEnd);
            } else {
                value = "";
            }

            if (!value.isEmpty()) {
                System.out.println(variable + " = " + value);
            }
            index = varEnd + 1;
        }
        System.out.println("============================\n");
    }
    
    private static String makeAPICall(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}