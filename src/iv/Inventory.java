package iv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brdley W. Duderstadt
 * @version 1.0
 */

public class Inventory {
    List<Product> products = new ArrayList<>();
    
    Inventory() {
    }
    
    public void addProduct(Product prod) {
      products.add(prod);
    }
    
    public Product lookupProduct(int prodID) throws IVException {
        Product theProd = null;
        
        
        if (prodID < 1) {
            IVException e = new IVException();
            e.setHeader("Invalid Product ID");
            e.setContent("Invalid product ID provided");
            throw e;
        }

        for (Product prod : products) {
            if (prod.getProductID() == prodID) {
                theProd = prod;
            }
        }
        
        if (theProd == null) {
            IVException e = new IVException();
            e.setHeader("Not Found");
            e.setContent("Product ID: " + prodID + " was not found");
            throw e;
        }
        else {
            return theProd;
        }
    }
    
    public boolean removeProduct(int prodID) {
        boolean result;
        result = products.removeIf(x -> x.getProductID() == prodID && x.getParts().isEmpty());
        return result;
    }
    
    public void updateProduct(Product prod) {
        removeProduct(prod.getProductID());
        addProduct(prod);
    }  
}
