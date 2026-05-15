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
        
        System.out.println("Enter vehicle VIN:");
        String vin = scanner.nextLine();
        
        List<Part> parts = getPartsFromVIN(vin);
        
        if(parts.isEmpty()) {
            System.out.println("No vehicle found or invalid VIN");
        } else {
            System.out.println("\nVehicle Parts for VIN: " + vin);
            System.out.println("================================================");
            for(Part part : parts) {
                System.out.println("Part: " + part.partName);
                System.out.println("Part Number: " + part.partNumber);
                System.out.println("------------------------------------------------");
            }
        }
        
        scanner.close();
    }
    
    public static List<Part> getPartsFromVIN(String vin) throws Exception {
        List<Part> parts = new ArrayList<>();
        
        try {
            // First, decode the VIN to get year, make, model
            String decodeUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/" + vin + "?format=json";
            String vehicleInfo = makeAPICall(decodeUrl);
            
            // Debug: print all variables to see what's available
            printAvailableVariables(vehicleInfo);
            
            // Extract from "Results" array - try different names
            /*String year = extractFromResultsArray(vehicleInfo, "Model Year");
            if(year.isEmpty()) year = extractFromResultsArray(vehicleInfo, "Year");
            
            String make = extractFromResultsArray(vehicleInfo, "Make");
            
            String model = extractFromResultsArray(vehicleInfo, "Model");
            
            System.out.println("Extracted - Year: '" + year + "', Make: '" + make + "', Model: '" + model + "'");
            
            if(year.isEmpty() || make.isEmpty() || model.isEmpty()) {
                System.out.println("Could not decode VIN - missing year, make, or model");
                return parts;
            }
            
            System.out.println("Found: " + year + " " + make + " " + model);
            
            // Now use GetParts API with the vehicle info
            String partsUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/GetParts?format=json&type=543&fromyear=" 
                            + year + "&toyear=" + year;
            
            String partsResponse = makeAPICall(partsUrl);
            String vinSuffix = vin.substring(Math.max(0, vin.length() - 4));
            System.out.println(partsResponse);
            
            // Parse parts from response - split by each part object
            String[] partObjects = partsResponse.split("\\{");
            for(int i = 1; i < partObjects.length; i++) {
                String partName = extractJsonValue(partObjects[i], "Part_Name");
                String partNumber = extractJsonValue(partObjects[i], "Part_Number");
                
                if(!partName.isEmpty()) {
                    if(partNumber.isEmpty()) {
                        partNumber = vinSuffix + "-" + String.format("%03d", i);
                    }
                    parts.add(new Part(partName, partNumber));
                }
            } 
            
            // Fallback to default parts if none found
            if(parts.isEmpty()) {
                parts.add(new Part("Engine Oil Filter", "OIL-" + vinSuffix + "-001"));
                parts.add(new Part("Air Filter", "AIR-" + vinSuffix + "-002"));
                parts.add(new Part("Spark Plugs", "SPK-" + vinSuffix + "-003"));
                parts.add(new Part("Brake Pads", "BPD-" + vinSuffix + "-004"));
                parts.add(new Part("Battery", "BAT-" + vinSuffix + "-005"));
                parts.add(new Part("Tires", "TIR-" + vinSuffix + "-006"));
            }*/
            
        } catch(Exception e) {
            System.out.println("Error calling API: " + e.getMessage());
            e.printStackTrace();
        }
        
        return parts;
    }
    
    private static void printAvailableVariables(String json) {
        System.out.println("\n=== Available Variables ===");
        int index = 0;
        while((index = json.indexOf("\"Variable\":\"", index)) != -1) {
            int start = index + 12;
            int end = json.indexOf("\"", start);
            String variable = json.substring(start, end);
            
            int valueIdx = json.indexOf("\"Value\":\"", end);
            int valueStart = valueIdx + 9;
            int valueEnd = json.indexOf("\"", valueStart);
            String value = json.substring(valueStart, valueEnd);
            
            System.out.println(variable + " = " + value);
            index = end + 1;
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
    
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int startIndex = json.indexOf(searchKey);
        if(startIndex == -1) return "";
        
        int colonIndex = json.indexOf(":", startIndex);
        if(colonIndex == -1) return "";
        
        int quoteStart = json.indexOf("\"", colonIndex);
        if(quoteStart == -1) return "";
        
        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        if(quoteEnd == -1) return "";
        
        return json.substring(quoteStart + 1, quoteEnd);
    }
    
    private static String extractFromResultsArray(String json, String variableName) {
    // Isolate the individual result objects to prevent pointer drifting
    String[] objects = json.split("\\}\\,\\{"); 
    
    for (String obj : objects) {
        // Double-check this specific block belongs to our target variable
        if (obj.contains("\"Variable\":\"" + variableName + "\"")) {
            
            // Safe extraction inside the isolated block
            int valueIdx = obj.indexOf("\"Value\":\"");
            if (valueIdx == -1) return ""; // Explicitly return empty if Value isn't here
            
            int start = valueIdx + 9;
            int end = obj.indexOf("\"", start);
            if (end != -1) {
                String foundValue = obj.substring(start, end).trim();
                // Ignore literal null strings or placeholders from the API
                if (foundValue.equalsIgnoreCase("null") || foundValue.isEmpty()) {
                    return "";
                }
                return foundValue;
            }
        }
    }
    return "";
    }
}
