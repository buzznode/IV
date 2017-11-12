package iv;

import java.util.Map;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * @author Bradley W. Duderstadt
 * @version 1.0.0
 */

public class ModPartController implements Initializable {
    
    /******************************/
    /***    Screen Variables    ***/
    /******************************/
    
    // Buttons
    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    // Controllers
    private final MainController mainCtrl;
    
    // Labels
    @FXML private Label lblVariable;
    
    // Radio Buttons
    @FXML private RadioButton radInhouse;
    @FXML private RadioButton radOutsourced;
    
    // Radio Groups
    @FXML ToggleGroup radGroup;

    // Stages
    private Stage stage;
    
    // TextFields
    @FXML private TextField txtPartID;
    @FXML private TextField txtInstock;
    @FXML private TextField txtMax;
    @FXML private TextField txtMin;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtVariable;

    /**********************************/
    /***    Non-Screen Variables    ***/
    /**********************************/

    private String companyName = "";
    private String machineID;
    private Map p = new HashMap();
//    private String origType;
    private final int partID;
    private Inhouse recIH;
    private Outsourced recOS;
    private String type;

    /**
     * Constructor
     * @param params 
     */
    public ModPartController(Map params) {
        this.p = params;
        this.mainCtrl = (MainController) p.get("mainCtrl");
        this.partID = Integer.parseInt(p.get("partID").toString());
        this.type = p.get("type").toString();
//        this.origType = type;
    }
    
    /**
     * Initializes Modify Part Controller
     * @param url - Uniform Resource Locator
     * @param rb  - Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnCancel.setOnAction((ActionEvent ae) -> {
            if (confirm("Cancel")) {
                stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });
        
        // Save button handler
        btnSave.setOnAction((ActionEvent ae) -> {
            try {
                saveData();
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
        
        // Radio Group handler
        radGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle ot, Toggle nt) -> {
            RadioButton chk = (RadioButton) nt.getToggleGroup().getSelectedToggle();

            if ("radInhouse".equals(chk.getId())) {
                // If radOutsourced was previously selected, save the txtVariable
                // string to the companyName variable before changing to Inhouse
                
                type = "I";
                companyName = txtVariable.getText();
                lblVariable.setText("Machine ID");
                txtVariable.clear();
                
                if (machineID == null) {
                }
                else {
                    txtVariable.setText(machineID); 
                }
            }
            else {
                // If radInhouse was previously setlected, save the txtVariable
                // string to the machineID variable before changing to outsourced
                
                type = "O";
                machineID = txtVariable.getText();
                lblVariable.setText("Company Name");
                txtVariable.clear();

                if (companyName == null) {
                }
                else {
                    txtVariable.setText(companyName); 
                }
            }
        });
        
        try {
            if ("I".equals(type)) {
                    recIH = mainCtrl.getIHRecord(partID);
                    machineID = Integer.toString(recIH.getMachineID());
                    companyName = null;
                    populateScreen(recIH);
            }
            else {
                recOS = mainCtrl.getOSRecord(partID);
                companyName = recOS.getCompanyName();
                machineID = null;
                populateScreen(recOS);
            }
        
            txtName.requestFocus();
        }
        catch (IVException ex) {
            ex.handler();
        }
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
     * Populates the Modify Part screen with an In-House Part data.
     * @param rec
     * @throws IVException 
     */
    private void populateScreen(Inhouse rec) throws IVException {
        txtPartID.setText(Integer.toString(rec.getPartID()));
        txtPartID.setDisable(true);
        txtName.setText(rec.getName());
        txtInstock.setText(Integer.toString(rec.getInstock()));
        txtPrice.setText(Double.toString(rec.getPrice()));
        txtMax.setText(Integer.toString(rec.getMax()));
        txtMin.setText(Integer.toString(rec.getMin()));
        txtVariable.setText(Integer.toString(rec.getMachineID()));
        radInhouse.setSelected(true);
    }

    /**
     * Populates the Modify Part screen with Outsourced Part data.
     * @param rec
     * @throws IVException 
     */
    private void populateScreen(Outsourced rec) throws IVException {
        txtPartID.setText(Integer.toString(rec.getPartID()));
        txtPartID.setDisable(true);
        txtName.setText(rec.getName());
        txtInstock.setText(Integer.toString(rec.getInstock()));
        txtPrice.setText(Double.toString(rec.getPrice()));
        txtMax.setText(Integer.toString(rec.getMax()));
        txtMin.setText(Integer.toString(rec.getMin()));
        txtVariable.setText(rec.getCompanyName());
        radOutsourced.setSelected(true);
    }
    
    /**
     * Saves Part data.
     * @throws IVException 
     */
    private void saveData() throws IVException, NumberFormatException {
        String content = "";
        
        if (txtPartID.getText().isEmpty()
        ||  txtName.getText().isEmpty()
        ||  txtInstock.getText().isEmpty()
        ||  txtPrice.getText().isEmpty()
        ||  txtMax.getText().isEmpty()
        ||  txtMin.getText().isEmpty()
        ||  txtVariable.getText().isEmpty()) {
            IVException e = new IVException();
            e.setHeader("Missing Required Field(s)");
            
            if (txtPartID.getText().isEmpty()) {
                content += "Part ID is a required field.\n";
            }
            if (txtName.getText().isEmpty()) {
                content += "Name is a required field.\n";
            }
            if (txtInstock.getText().isEmpty()) {
                content += "Inv (Instock) is a required field.\n";
            }
            if (txtPrice.getText().isEmpty()) {
                content += "Price is a required field.\n";
            }
            if (txtMax.getText().isEmpty()) {
                content += "Max is a required field.\n";
            }
            if (txtMin.getText().isEmpty()) {
                content += "Min is a required field.\n";
            }
            if (txtVariable.getText().isEmpty()) {
                if (type.equals("I")) {
                    content += "Machine ID is a required field.\n";
                }
                else {
                    content += "Company Name is a required field.\n";
                }
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
        
        mainCtrl.deletePartByID(partID);

        if ("I".equals(type)) {
            Inhouse part = new Inhouse();
            part.setPartID(partID);
            part.setName(txtName.getText());
            part.setInstock(Integer.parseInt(txtInstock.getText()));
            part.setPrice(Double.parseDouble(txtPrice.getText()));
            part.setMax(Integer.parseInt(txtMax.getText()));
            part.setMin(Integer.parseInt(txtMin.getText()));
            part.setMachineID(Integer.parseInt(txtVariable.getText()));
            mainCtrl.inhouse.add(part);
        }
        else {
            Outsourced part = new Outsourced();
            part.setPartID(partID);
            part.setName(txtName.getText());
            part.setInstock(Integer.parseInt(txtInstock.getText()));
            part.setPrice(Double.parseDouble(txtPrice.getText()));
            part.setMax(Integer.parseInt(txtMax.getText()));
            part.setMin(Integer.parseInt(txtMin.getText()));
            part.setCompanyName(txtVariable.getText());
            mainCtrl.outsourced.add(part);
        }
    }
}