import java.util.ArrayList;
public class Car{
    private String make;
    private String model;
    private int year = 0;
    private ArrayList<String> vinList;

    public Car(String make, String model, int year, String vin){
        if(this.make == null && this.model ==null && this.year == 0){
            this.make = make;
            this.model = model;
            this.year = year;
            this.vinList.add(vin);
        }
        else{
            System.out.println("Car already exists. Adding VIN to existing car.");
            this.vinList.add(vin);
            System.out.println(vinList);
        }
    }

    public void addPartsToStock(String partname, int numOfParts){
        if(this.partname != null && this.partname.equals(partname)){
            stock += numOfParts;
        }
        else{
            System.out.println("Invalid part");
        }
    }


}
