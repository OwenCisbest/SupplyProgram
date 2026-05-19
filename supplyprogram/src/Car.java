import java.util.ArrayList;

public class Car {
    public String name;
    private String make;
    private String model;
    private int year;
    private ArrayList<String> vinList;

    public Car(String make, String model, int year, String vin) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vinList = new ArrayList<>();
        this.vinList.add(vin);
        this.name = year + "_" + make + "_" + model;
    }

    public void addVin(String vin) {
        if (vinList.contains(vin)) {
            System.out.println("VIN already exists: " + vin);
        } else {
            vinList.add(vin);
            System.out.println("Added VIN: " + vin);
            System.out.println("All VINs: " + vinList);
        }
    }

    public ArrayList<String> getVins(){
        return vinList;
    }

    @Override
    public String toString() {
        return name;
    }
}