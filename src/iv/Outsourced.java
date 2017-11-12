package iv;

/**
 * @author Bradley W. Duderstadt
 */

public class Outsourced extends Part {
    private String companyName;
    
    public Outsourced(int partID, 
               String name, 
               int instock, 
               double price, 
               int max, 
               int min, 
               String companyName) {
        this.setPartID(partID);
        this.setName(name);
        this.setInstock(instock);
        this.setPrice(price);
        this.setMax(max);
        this.setMin(min);
        this.setCompanyName(companyName);
    }
    
    public Outsourced() {
    }
    
    public String getCompanyName() {
        return this.companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
