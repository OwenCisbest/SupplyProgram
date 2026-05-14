public class Supply {
    private String partname;
    private int stock;

    public void Part(String partname){
        this.partname = partname;
        this.stock = 0;
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
