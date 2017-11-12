package iv;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * @author Bradley W. Duderstadt
 * @version 1.0
 */

public class AddProdController implements Initializable {
    
    /******************************/
    /***    Screen Variables    ***/
    /******************************/
    
    // Buttons
    @FXML private Button btnAdd;
    @FXML private Button btnCancel;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnSearch;

    // Stages
    Stage stage;
    
    // TextFields
    @FXML private TextField txtInstock;
    @FXML private TextField txtMax;
    @FXML private TextField txtMin;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtProdID;
    @FXML private TextField txtSearch;

    // Available Part Table & Columns
    @FXML TableView<Part> tblAvail;
    @FXML TableColumn colAvailInstock;
    @FXML TableColumn colAvailName;
    @FXML TableColumn colAvailPartID;
    @FXML TableColumn colAvailPrice;
    
    // Used Part Table & Columns
    @FXML TableView<Part> tblUsed;
    @FXML TableColumn colUsedInstock;
    @FXML TableColumn colUsedName;
    @FXML TableColumn colUsedPartID;
    @FXML TableColumn colUsedPrice;

    /**********************************/
    /***    Non-Screen Variables    ***/
    /**********************************/

    // Controllers
    private final MainController mainCtrl;

    // Miscellaneous
    private int instock;
    private String name;
    private Map p = new HashMap();
    private int partID;
    private double price;
    private final int prodID;

    // Observable Lists
    private final ObservableList<Part> avail = FXCollections.observableArrayList();
    private final ObservableList<Part> used = FXCollections.observableArrayList();

    /**
     * Constructor
     * @param params 
     */
    public AddProdController(Map params) {
        this.p = params;
        this.mainCtrl = (MainController) p.get("mainCtrl");
        this.prodID = Integer.parseInt(p.get("prodID").toString());
    }
    
    /**
     * Initializes Add Product Controller
     * @param url - Uniform Resource Location
     * @param rb  - Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colAvailPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        colAvailName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAvailInstock.setCellValueFactory(new PropertyValueFactory<>("instock"));
        colAvailPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Right-align numerics
        colAvailInstock.setStyle("-fx-alignment: CENTER-RIGHT");
        colAvailPrice.setStyle("-fx-alignment: CENTER-RIGHT");

        colUsedPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        colUsedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUsedInstock.setCellValueFactory(new PropertyValueFactory<>("instock"));
        colUsedPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Right-align numerics
        colUsedInstock.setStyle("-fx-alignment: CENTER-RIGHT");
        colUsedPrice.setStyle("-fx-alignment: CENTER-RIGHT");
        
        // Add button handler
        btnAdd.setOnAction ((ActionEvent ae) -> {
            try {
                addPartToProd();
            }
            catch (IVException ex) {
                ex.handler();
            }
        });
        
        // Cancel button handler
        btnCancel.setOnAction((ActionEvent ae) -> {
            if (confirm("Cancel")) {
                try {
                    stage = (Stage) btnCancel.getScene().getWindow();
                    stage.close();
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
        // Delete button handler
        btnDelete.setOnAction((ActionEvent ae) -> {
            if (confirm("Delete")) {
                try {
                    deletePartFromProd();
                }
                catch (IVException ex) {
                    ex.handler();
                }
            }
        });
        
        // Save button handler
        btnSave.setOnAction((ActionEvent ae) -> {
            try {
                saveProd();
                stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            }
            catch (NumberFormatException ex) {
                IVException e = new IVException();
                e.setHeader("Number Format Exception");
                e.setContent("There was an error parsing numeric data.");
                e.handler();
            }
            catch (IVException ex) {
                ex.handler();
            }
        });
        
        // Search button handler
        btnSearch.setOnAction((ActionEvent ae) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dynamic Search");
            alert.setHeaderText("Dynamic Search Enabled");
            alert.setContentText("This application utilizes dynamic search via filtering. Simply type your search criteria in the textfield provided and the list will be automatically filtered.");
            alert.showAndWait(); 
        });
        
        loadAvail();
        tblAvail.setItems(addFilterSortListeners(avail));
        txtProdID.setText(Integer.toString(prodID));
        txtProdID.setDisable(true);
        txtPrice.setText("0.00");
        txtName.requestFocus();
    }
 
    private SortedList<Part> addFilterSortListeners(ObservableList<Part> ol) {
        FilteredList<Part> filteredPartData = new FilteredList<>(ol, x -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
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
        return sortedPartData;
    }
    
    /**
     * Adds Part to Product.
     * @throws IVException 
     */
    private void addPartToProd() throws IVException {
        if (tblAvail.getSelectionModel().getSelectedItem() == null) {
            IVException e = new IVException();
            e.setHeader("NullPointer");
            e.setContent("No part has been selected.");
            throw e;
        }
        
        partID = tblAvail.getSelectionModel().getSelectedItem().getPartID();
        used.add(tblAvail.getSelectionModel().getSelectedItem());
        
        for (Iterator<Part> iterator = avail.iterator(); iterator.hasNext(); ) {
            Part part = iterator.next();
            if (part.getPartID() == partID) {
                iterator.remove();
            }
        }
        
        tblAvail.setItems(addFilterSortListeners(avail));
        tblUsed.setItems(addFilterSortListeners(used));
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
     * Deletes Part from Product.
     * @throws IVException 
     */
    private void deletePartFromProd() throws IVException {
        if (tblUsed.getSelectionModel().getSelectedItem() == null) {
            IVException e = new IVException();
            e.setHeader("NullPointer");
            e.setContent("No part has been selected.");
            throw e;
        }
        
        partID = tblUsed.getSelectionModel().getSelectedItem().getPartID();
        avail.add(tblUsed.getSelectionModel().getSelectedItem());
        
        for (Iterator<Part> iterator = used.iterator(); iterator.hasNext(); ) {
            Part part = iterator.next();
            if (part.getPartID() == partID) {
                iterator.remove();
            }
        }
        
        tblAvail.setItems(addFilterSortListeners(avail));
        tblUsed.setItems(addFilterSortListeners(used));
    }
    
    /**
     * Loads Available ObservableList
     */
    private void loadAvail() {
        avail.clear();
        used.clear();
        avail.addAll(mainCtrl.partData);
    }
    
    /**
     * Saves Product
     * @throws IVException 
     */
    private void saveProd() throws IVException, NumberFormatException {
        String content = "";
        Double sum = 0.00;
        
        if (txtName.getText().isEmpty()
        ||  txtInstock.getText().isEmpty()
        ||  txtPrice.getText().isEmpty()
        ||  txtMax.getText().isEmpty()
        ||  txtMin.getText().isEmpty()) {
            IVException e = new IVException();
            e.setHeader("Missing Required Input");
            
            if (txtName.getText().isEmpty()) {
                content += "Name is a required field.\n";
            }
            if (txtInstock.getText().isEmpty()) {
                content += "Inventory (Instock) is a required field.\n";
            }
            if (txtPrice.getText().isEmpty()) {
                content += "Price is a required field.\n";
            }
            if (txtMax.getText().isEmpty()) {
                content += "Maximum is a required field.\n";
            }
            if (txtMin.getText().isEmpty()) {
                content += "Minimum is a required field.\n";
            }
            
            e.setContent(content);
            throw e;
        }
        
        int iInstock = Integer.parseInt(txtInstock.getText());
        int iMax = Integer.parseInt(txtMax.getText());
        int iMin = Integer.parseInt(txtMin.getText());
        
        if (iInstock > iMax) {
            IVException e = new IVException();
            e.setHeader("Invalid Value");
            e.setContent("Invalid Inventory Level. It cannot exceed Maximum value.\nInventory Level: " + iInstock + "\nMaximum: " + iMax);
            throw e;
        }
        if (iInstock < iMin) {
            IVException e = new IVException();
            e.setHeader("Invalid Value");
            e.setContent("Invalid Inventory Level. It cannot be less than Minimum value.\nInventory Level: " + iInstock + "\nMinimum: " + iMin);
            throw e;
        }
        if (iMin > iMax) {
            IVException e = new IVException();
            e.setHeader("Invalid Value");
            e.setContent("Minimum value cannot exceed Maximum value.\nMinimum: " + iMin + "\nMaximum: " + iMax);
            throw e;
        }
        if (iMax < iMin) {
            IVException e = new IVException();
            e.setHeader("Invalid Value");
            e.setContent("Maximum value cannot be less than Minimum value.\nMaximum: " + iMax + "\nMinimum: " + iMin);
            throw e;
        }
        
        if (used.isEmpty()) {
            IVException e = new IVException();
            e.setHeader("Missing Parts");
            e.setContent("You must add at least 1 part to the product.");
            throw e;
        }
        
        for (Part part : used) {
            sum += part.getPrice();
        }
        
        if (Double.parseDouble(txtPrice.getText()) < sum) {
            IVException e = new IVException();
            e.setHeader("Invalid Price");
            e.setContent("The price of the product (" + txtPrice.getText() + ") is less than the accumulated parts list (" + sum + "). Please correct and try again.");
            throw e;
        }
        
        Product prod = new Product();
        prod.setProductID(Integer.parseInt(txtProdID.getText()));
        prod.setName(txtName.getText());
        prod.setInstock(Integer.parseInt(txtInstock.getText()));
        prod.setPrice(Double.parseDouble(txtPrice.getText()));
        prod.setMax(Integer.parseInt(txtMax.getText()));
        prod.setMin(Integer.parseInt(txtMin.getText()));

        used.forEach((Part item) -> {
           prod.addPart(item);
        });
        
        mainCtrl.iv.addProduct(prod);
    }
}
