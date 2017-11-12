package iv;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
 
/**
 * @author Bradley W. Duderstadt
 * @version 1.0
 */

public class MainController implements Initializable {
    
    /******************************/
    /***    Screen Variables    ***/
    /******************************/
    
    // Buttons
    @FXML private Button btnAddPart;
    @FXML private Button btnAddProd;
    @FXML private Button btnDelPart;
    @FXML private Button btnDelProd;
    @FXML private Button btnExit;
    @FXML private Button btnModPart;
    @FXML private Button btnModProd;
    @FXML private Button btnPartSearch;
    @FXML private Button btnProdSearch;

    // Tables & Columns
    @FXML TableView<Part> tblPart;
    
    @FXML TableColumn colPartID;
    @FXML TableColumn colPartName;
    @FXML TableColumn colPartInstock;
    @FXML TableColumn colPartPrice;

    @FXML TableView<Product> tblProd;

    @FXML TableColumn colProdID;
    @FXML TableColumn colProdName;
    @FXML TableColumn colProdInstock;
    @FXML TableColumn colProdPrice;

    // TextFields
    @FXML private TextField txtPartSearch;
    @FXML private TextField txtProdSearch;
    
    /*** Non-Screen Elements ***/
    
    // Controllers
    private AddPartController addPartCtrl;
    private ModPartController modPartCtrl;
    private AddProdController addProdCtrl;
    private ModProdController modProdCtrl;

    // Lists & ArrayLists
    public List<Inhouse> inhouse = new ArrayList<>();
    public List<Outsourced> outsourced = new ArrayList<>();
    public List<Product> product = new ArrayList<>();

    // Observable Lists
    public ObservableList<Part> partData = FXCollections.observableArrayList();
    public ObservableList<Product> prodData = FXCollections.observableArrayList();
    
    // Miscellaneous
    private FXMLLoader loader;
    private final Map p = new HashMap();
    private Stage pop;
    private final boolean preload = true;
    private Stage primaryStage;
    
    // Objects
    public Inventory iv = new Inventory();

    /**
     * This method initializes the MainController for use. In essence, it is
     * the driver method for the controller.
     * @param url - Uniform Resource Location
     * @param rb  - Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        colPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPartInstock.setCellValueFactory(new PropertyValueFactory<>("instock"));
        colPartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Set alignment for numerics
        colPartInstock.setStyle("-fx-alignment: CENTER-RIGHT");
        colPartPrice.setStyle("-fx-alignment: CENTER-RIGHT");
        
        colProdID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdInstock.setCellValueFactory(new PropertyValueFactory<>("instock"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Set alignment for numerics
        colProdInstock.setStyle("-fx-alignment: CENTER-RIGHT");
        colProdPrice.setStyle("-fx-alignment: CENTER-RIGHT");
        
        // Preload data if desired
        if (preload) { 
            preloadData(); 
        }
        
        loadTables();

        // Add Part button handler
        btnAddPart.setOnAction((ActionEvent ae) -> {
            try {
                showAddPart();
            }
            catch (IVException ex) {
                ex.handler();
            }
            catch (IOException ex) {
                IVException e = new IVException();
                e.setHeader("IOException");
                e.setContent(ex.getMessage());
                e.handler();
            }
        });
        
        // Delete Part button handler
        btnDelPart.setOnAction((ActionEvent ae) -> {
            if (confirm("Delete")) {
                try {
                    deletePartByID(getSelectedPartID());
                }
                catch (IVException ex) {
                    ex.handler();
                }
            }
        });
        
        // Modify Part button handler
        btnModPart.setOnAction((ActionEvent ae) -> {
            try {               
                showModPart();
            }
            catch (IVException ex) {
                ex.handler();
            }
            catch (IOException ex) {
                IVException e = new IVException();
                e.setHeader("IOException");
                e.setContent(ex.getMessage());
                e.handler();
            }
        });
        
        // Add Product button handler
        btnAddProd.setOnAction((ActionEvent ae) -> {
            try {
                showAddProd();
            }
            catch (IVException ex) {
                ex.handler();
            }
            catch (IOException ex) {
                IVException e = new IVException();
                e.setHeader("IOException");
                e.setContent(ex.getMessage());
                e.handler();
            }
        });

        // Delete Product button handler
        btnDelProd.setOnAction((ActionEvent ae) -> {
            if (confirm("Delete")) {
                try {
                    if (iv.removeProduct(getSelectedProdID())) {
                        loadTables();
                    }
                    else {
                        IVException e = new IVException();
                        e.setTitle("Deletion Failure");
                        e.setContent("This product cannot be deleted because it has one or more parts assigned to it. Please delete the parts first.");
                        e.handler();
                    }
                }
                catch (IVException ex) {
                    ex.handler();
                }
            }
        });
        
        // Modify Product button handler
        btnModProd.setOnAction((ActionEvent ae) -> {
            try {
                showModProd();
            }
            catch (IVException ex) {
                ex.handler();
            }
            catch (IOException ex) {
                IVException e = new IVException();
                e.setHeader("IOException");
                e.setContent(ex.getMessage());
                e.handler();
            }
        });
        
        // Part Search button handler
        btnPartSearch.setOnAction((ActionEvent ae) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dynamic Search");
            alert.setHeaderText("Dynamic Search Enabled");
            alert.setContentText("This application utilizes dynamic search via filtering. Simply type your search criteria in the textfield provided and the list will be automatically filtered.");
            alert.showAndWait(); 
        });
        
        // Product Search button handler
        btnProdSearch.setOnAction((ActionEvent ae) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dynamic Search");
            alert.setHeaderText("Dynamic Search Enabled");
            alert.setContentText("This application utilizes dynamic search via filtering. Simply type your search criteria in the textfield provided and the list will be automatically filtered.");
            alert.showAndWait();
        });
        
        // Exit button handler
        btnExit.setOnAction((ActionEvent ae) -> {
            Platform.exit();
            System.exit(0);
        });
    } 

    /**
     * Confirms a given user action
     * @param confirmType
     * @return boolean value
     */
    private boolean confirm(String confirmType) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(confirmType + " Confirmation");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    /**
     * Deletes a given part.
     * @param partID
     * @throws IVException 
     */
    public void deletePartByID(int partID) throws IVException {
        inhouse.removeIf(x -> x.getPartID() == partID);
        outsourced.removeIf(x -> x.getPartID() == partID);
        loadTables();
    }
    
    /**
     * Retrieves a specific "In-house" part.
     * @param partID
     * @return In-house part.
     */
    public Inhouse getIHRecord(int partID) {
        boolean found = false;
        Iterator<Inhouse> iter = inhouse.iterator();
        Inhouse currItem = null;

        while (iter.hasNext()) {
            currItem = (Inhouse) iter.next();

            if (currItem.getPartID() == partID) {
                found = true;
                break;
            }
        }
        return found ? currItem : null;
    }

    /**
     * Determines the next Part ID that is to be used.
     * @return next Integer Part ID to be used.
     */
    public int getNextPartID() {
        int nextPartID = 0;

        if (partData.isEmpty()) {
            nextPartID = 1;
        }
        else {
            for (Part part : partData) {
                if (part.getPartID() >= nextPartID) {
                    nextPartID = part.getPartID() + 1;
                }
            }
        }
        return nextPartID;
    }
    
    /**
     * Determines the next Product ID that is to be used.
     * @return next Integer Product ID to be used.
     */
    public int getNextProdID() {
        int nextProdID = 0;

        if (prodData.isEmpty()) {
            nextProdID = 1;
        }
        else {
            for (Product prod : prodData) {
                if (prod.getProductID()>= nextProdID) {
                    nextProdID = prod.getProductID() + 1;
                }
            }
        }
        return nextProdID;
    }
    
    /**
     * Retrieves a specific "Outsourced" part.
     * @param partID
     * @return Outsourced part.
     */
    public Outsourced getOSRecord(int partID) {
        boolean found = false;
        Iterator<Outsourced> iter = outsourced.iterator();
        Outsourced currItem = null;
       
        while (iter.hasNext()) {
            currItem = (Outsourced) iter.next();
            if (currItem.getPartID() == partID) {
                found = true;
                break;
            }
        }
        return found ? currItem : null;
    }
    
    /**
     * Loads the partData ObservableList that is tied to the Part TableView.
     * It uses both In-house and Outsourced parts from their respective ArrayLists.
     * @return ObservableList of parts.
     */
    public ObservableList<Part> getParts() {
        partData.clear();
        partData.addAll(inhouse);
        partData.addAll(outsourced);
        return partData;
    }
    
    /**
     * Loads the prodData ObservableList that is tied to the Product TableView.
     * It uses the iv.products ArrayList to populate itself.
     * @return ObservableList of products.
     */
    public ObservableList<Product> getProducts() {
        prodData.clear();
        prodData.addAll(iv.products);
        return prodData;
    }

    /**
     * Retrieves the selected Part ID from the Part TableView.
     * @return Integer Part ID of selected Part.
     * @throws IVException 
     */
    public int getSelectedPartID() throws IVException {
        int partID;
        
        if (tblPart.getSelectionModel().getSelectedItem() == null) {
            IVException e = new IVException();
            e.setHeader("NullPointer");
            e.setContent("No part has been selected.");
            throw e;
        }

        partID = tblPart.getSelectionModel().getSelectedItem().getPartID();
        return partID;
    }
    
    /**
     * Retrieves the selected Product ID from the Product TableView.
     * @return Integer Product ID of selected Product.
     * @throws IVException 
     */
    public int getSelectedProdID() throws IVException {
        int prodID;
        
        if (tblProd.getSelectionModel().getSelectedItem() == null) {
            IVException e = new IVException();
            e.setHeader("NullPointer");
            e.setContent("No product has been selected");
            throw e;
        }
        
        prodID = tblProd.getSelectionModel().getSelectedItem().getProductID();
        return prodID;
    }
    
    /**
     * Loads the TableViews by calling the appropriate methods to retrieve the data.
     */
    public void loadTables() {
        txtPartSearch.setText(null);
        tblPart.setItems(null);
        tblProd.setItems(null);
        tblPart.setItems(getParts());
        tblProd.setItems(getProducts());

        FilteredList<Part> filteredPartData = new FilteredList<>(partData, x -> true);

        txtPartSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPartData.setPredicate(part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCasePartFilter = newValue.toLowerCase();

                if (Integer.toString(part.getPartID()).contains(lowerCasePartFilter)) {
                    return true;
                }
                else if (part.getName().toLowerCase().contains(lowerCasePartFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Part> sortedPartData = new SortedList<>( filteredPartData, 
        (Part part1, Part part2) -> {
            String s1 = part1.getName();
            String s2 = part2.getName();
            int result;
            result = s1.compareTo(s2);
            
            if (result < 0) {
                return -1;
            } 
            else if (result > 0) {
                return 1;
            } 
            else {
                return 0;
            }
        });        

        tblPart.setItems(sortedPartData);
        
        FilteredList<Product> filteredProdData = new FilteredList<>(prodData, x -> true);

        txtProdSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProdData.setPredicate(prod -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseProdFilter = newValue.toLowerCase();

                if (Integer.toString(prod.getProductID()).contains(lowerCaseProdFilter)) {
                    return true;
                }
                else if (prod.getName().toLowerCase().contains(lowerCaseProdFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Product> sortedProdData = new SortedList<>( filteredProdData, 
        (Product prod1, Product prod2) -> {
            String s1 = prod1.getName();
            String s2 = prod2.getName();
            int result;
            result = s1.compareTo(s2);
            
            if (result < 0) {
                return -1;
            } 
            else if (result > 0) {
                return 1;
            } 
            else {
                return 0;
            }
        });        

        tblProd.setItems(sortedProdData);
    }
    
    /**
     * Loads sample data (if desired)
     */
    private void preloadData() {
        inhouse.add(new Inhouse(1, "IH Part One", 100, 4.79, 1000, 20, 1470));
        inhouse.add(new Inhouse(2, "IH Part Two", 64, 8.68, 450, 10, 1500));
        inhouse.add(new Inhouse(3, "IH Part Three", 5, 2.47, 500, 2, 4999));

        outsourced.add(new Outsourced(4, "OS Part One", 23, 15.99, 1250, 130, "Teflon Is Us"));
        outsourced.add(new Outsourced(5, "OS Part Two", 50, 3.29, 750, 100, "Wanda's Widgets"));
        outsourced.add(new Outsourced(6, "OS Part Three", 147, 134.99, 9800, 250, "Excelleron"));
    }
    
    /**
     * Sets the Primary Stage local variable.
     * @param primaryStage 
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Sets up and displays the Add Part screen and waits.
     * @throws IVException
     * @throws IOException 
     */
    public void showAddPart() throws IVException, IOException {
        loader = new FXMLLoader(getClass().getResource("AddPart.fxml"));
        p.clear();
        p.put("partID", getNextPartID());
        p.put("type", "I");
        p.put("mainCtrl", this);
        addPartCtrl = new AddPartController(p);
        loader.setController(addPartCtrl);
        pop = new Stage();
        pop.initOwner(primaryStage);
        pop.setScene(new Scene(new BorderPane(loader.load())));
        pop.initModality(Modality.APPLICATION_MODAL);
        addPartCtrl.displayPartID();
        pop.showAndWait();
        loadTables();
    }
    
    /**
     * Sets up and displays the Add Product screen and waits.
     * @throws IVException
     * @throws IOException 
     */
    public void showAddProd() throws IVException, IOException {
        loader = new FXMLLoader(getClass().getResource("AddProd.fxml"));
        p.clear();
        p.put("prodID", getNextProdID());
        p.put("mainCtrl", this);
        addProdCtrl = new AddProdController(p);
        loader.setController(addProdCtrl);
        pop = new Stage();
        pop.initOwner(primaryStage);
        pop.setScene(new Scene(new BorderPane(loader.load())));
        pop.initModality(Modality.APPLICATION_MODAL);
        pop.showAndWait();
        loadTables();
    }

    /**
     * Sets up and displays the Modify Part screen and waits.
     * @throws IVException
     * @throws IOException 
     */
    public void showModPart() throws IVException, IOException {
        String type;
        loader = new FXMLLoader(getClass().getResource("ModPart.fxml"));
        p.clear();
        p.put("partID", getSelectedPartID());
        p.put("mainCtrl", this);

        type = "class iv.Inhouse".equals(tblPart.getSelectionModel().getSelectedItem().getClass().toString()) ? "I" : "O";
        p.put("type", type);

        modPartCtrl = new ModPartController(p);
        loader.setController(modPartCtrl);
        pop = new Stage();
        pop.initOwner(primaryStage);
        pop.setScene(new Scene(new BorderPane(loader.load())));
        pop.initModality(Modality.APPLICATION_MODAL);
        pop.showAndWait();
        loadTables();
    }
    
    /**
     * Sets up and displays the Modify Product screen and waits.
     * @throws IVException
     * @throws IOException 
     */
    public void showModProd() throws IVException, IOException {
        loader = new FXMLLoader(getClass().getResource("ModProd.fxml"));
        p.clear();
        p.put("prodID", getSelectedProdID());
        p.put("mainCtrl", this);
        modProdCtrl = new ModProdController(p);
        loader.setController(modProdCtrl);
        pop = new Stage();
        pop.initOwner(primaryStage);
        pop.setScene(new Scene(new BorderPane(loader.load())));
        pop.initModality(Modality.APPLICATION_MODAL);
        pop.showAndWait();
        loadTables();
    }
}
