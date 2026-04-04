package com.cs210.project;

import com.cs210.project.dao.VehicleDAO;
import com.cs210.project.models.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CreateVehicleController {
    
    @FXML private TextField locationIdField;
    @FXML private TextField barcodeIdField;
    @FXML private ComboBox<String> vehicleTypeCombo;
    @FXML private TextField licenseNumberField;
    @FXML private TextField stockNumberField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        vehicleTypeCombo.getItems().addAll("car", "truck", "suv", "van", "motorcycle");
        statusCombo.getItems().addAll("available", "reserved", "loaned", "lost", "in_maintenance", "other"); // Note: You can align these with your exact DB enum
    }

    @FXML
    protected void onSaveButtonClick() {
        try {
            Vehicle v = new Vehicle();
            
            // Required inputs parsing
            v.setLocationId(Long.parseLong(locationIdField.getText()));
            v.setBarcodeId(Long.parseLong(barcodeIdField.getText()));
            v.setVehicleType(vehicleTypeCombo.getValue());
            v.setLicenseNumber(licenseNumberField.getText());
            v.setStockNumber(stockNumberField.getText());
            v.setStatus(statusCombo.getValue());
            
            // Optional inputs logic
            if (!makeField.getText().isEmpty()) v.setMake(makeField.getText());
            if (!modelField.getText().isEmpty()) v.setModel(modelField.getText());
            if (!yearField.getText().isEmpty()) v.setManufacturingYear(Integer.parseInt(yearField.getText()));

            // Use the DAO to save the vehicle to the DB
            VehicleDAO dao = new VehicleDAO();
            dao.create(v);

            messageLabel.setText("Vehicle Created Successfully! ID: " + v.getId());
            messageLabel.setStyle("-fx-text-fill: green;");
            clearForm();
        } catch (NumberFormatException nfe) {
            messageLabel.setText("Please enter valid numbers for Location, Barcode, and Year.");
            messageLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            messageLabel.setText("Error saving vehicle. Check values or console.");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
    
    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) locationIdField.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        locationIdField.clear();
        barcodeIdField.clear();
        vehicleTypeCombo.setValue(null);
        licenseNumberField.clear();
        stockNumberField.clear();
        statusCombo.setValue(null);
        makeField.clear();
        modelField.clear();
        yearField.clear();
    }
}
