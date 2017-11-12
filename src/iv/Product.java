package iv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bradley W. Duderstadt
 * @version 1.0
 */

public final class Product {
    private int instock;
    private int max;
    private int min;
    private String name;
    private List<Part> parts = new ArrayList<>();
    private double price;
    private int productID;
    
    public Product() { 
    }
    
    public Product(int productID, String name, int instock, double price, int max, int min) {
        setProductID(productID);
        setName(name);
        setInstock(instock);
        setPrice(price);
        setMax(max);
        setMin(min);
    }

    /* Getters */
    
    public int getInstock() {
        return this.instock;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Part> getParts() {
        return parts;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public int getProductID() {
        return this.productID;
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
    
    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    /* Methods */
    
    public void addPart(Part part) {
        parts.add(part);
    }
    
    public boolean removePart(int partID) {
        try {
            parts.remove(partID);
            return true;
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    Part lookupPart(int partID) {
        return parts.get(0);
    }
    
    void updatePart(int partID) {
    }
}
