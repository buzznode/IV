package iv;

/**
 * @author Bradley W. Duderstadt
 */

public class Inhouse extends Part {
    private int machineID;
    
    public Inhouse(int partID, 
                   String name, 
                   int instock, 
                   double price, 
                   int max, 
                   int min, 
                   int machineID) { 
        this.setPartID(partID);
        this.setName(name);
        this.setInstock(instock);
        this.setPrice(price);
        this.setMax(max);
        this.setMin(min);
        this.setMachineID(machineID);
    }
    
    public Inhouse() {
    }
    
    public int getMachineID() {
        return this.machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    } 
}
