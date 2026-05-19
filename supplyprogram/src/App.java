import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class App {
    static Map<String, Car> cars = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter vehicle VIN (or 'exit' to quit):");
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) break;
            getCarInfo(input);
            System.out.println("Enter another VIN (or 'exit' to quit):");
        }
        scanner.close();
    }

    public static void getCarInfo(String vin) throws Exception {
        try {
            String decodeUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/" + vin + "?format=json";
            String json = makeAPICall(decodeUrl);
            System.out.println(json);

            printAvailableVariables(json);

            Car car = buildCar(json);
            if (cars.containsKey(car.name)) {
                cars.get(car.name).addVin(car.getVins().get(0));
            }
            else {
                cars.put(car.name, car);
            }
            System.out.println(cars.get(car.name).getVins());
            System.out.println("Stored: " + car.name);
            System.out.println("Current Cars in System:" + cars.values());
        } catch (Exception e) {
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

            int valueIdx = json.lastIndexOf("\"Value\":", index);
            if (valueIdx == -1) { index = varEnd + 1; continue; }

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

            if (!value.isEmpty()) System.out.println(variable + " = " + value);
            index = varEnd + 1;
        }
        System.out.println("============================\n");
    }

    private static Car buildCar(String json) {
        int index = 0;
        String make = "", model = "", vin = "";
        int year = 0;

        while ((index = json.indexOf("\"Variable\":\"", index)) != -1) {
            int varStart = index + 12;
            int varEnd = json.indexOf("\"", varStart);
            if (varEnd == -1) break;
            String variable = json.substring(varStart, varEnd);

            int valueIdx = json.lastIndexOf("\"Value\":", index);
            if (valueIdx == -1) { index = varEnd + 1; continue; }

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
                if (variable.equals("Make")) make = value;
                else if (variable.equals("Model")) model = value;
                else if (variable.equals("Model Year")) {
                    try { year = Integer.parseInt(value); }
                    catch (NumberFormatException e) { year = 0; }
                }
                else if (variable.equals("VIN")) vin = value;
            }
            index = varEnd + 1;
        }
        return new Car(make, model, year, vin);
    }

    private static String makeAPICall(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();
        return response.toString();
    }
}