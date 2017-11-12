package iv;

/**
 * @author Bradley W. Duderstadt
 */

public abstract class Part {
    private int instock;
    private int max;
    private int min;
    private String name;
    private int partID;
    private double price;
    
    /* Getters */
    
    public int getInstock() {
        return instock;
    }
    
    public int getMax() {
        return max;
    }
    
    public int getMin() {
        return min;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPartID() {
        return partID;
    }

    public double getPrice() {
        return price;
    }
    
    /* Setters */
    
    public void setInstock(int instock) {
        this.instock = instock;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPartID(int partID) {
        this.partID = partID;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }  
}

