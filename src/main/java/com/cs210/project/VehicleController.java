package com.cs210.project;

import com.cs210.project.dao.VehicleDAO;
import com.cs210.project.models.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class VehicleController {

    @FXML private TableView<Vehicle> vehicleTable;
    
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
    @FXML private Button deleteButton;

    private VehicleDAO vehicleDAO;
    private Vehicle selectedVehicle = null;

    @FXML
    public void initialize() {
        vehicleDAO = new VehicleDAO();
        refreshTable();

        vehicleTypeCombo.getItems().addAll("car", "truck", "suv", "van", "motorcycle");
        statusCombo.getItems().addAll("available", "reserved", "loaned", "lost", "in_maintenance", "other");
    }

    private void refreshTable() {
        List<Vehicle> list = vehicleDAO.findAll();
        ObservableList<Vehicle> observableList = FXCollections.observableArrayList(list);
        vehicleTable.setItems(observableList);
    }

    @FXML
    protected void onTableSelection(MouseEvent event) {
        Vehicle clicked = vehicleTable.getSelectionModel().getSelectedItem();
        if (clicked != null) {
            selectedVehicle = clicked;
            populateForm(clicked);
            deleteButton.setDisable(false);
            messageLabel.setText("Editing Mode active: Selected ID " + selectedVehicle.getId());
            messageLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }
    }

    private void populateForm(Vehicle v) {
        locationIdField.setText(String.valueOf(v.getLocationId()));
        barcodeIdField.setText(String.valueOf(v.getBarcodeId()));
        vehicleTypeCombo.setValue(v.getVehicleType());
        licenseNumberField.setText(v.getLicenseNumber());
        stockNumberField.setText(v.getStockNumber());
        statusCombo.setValue(v.getStatus());
        
        makeField.setText(v.getMake() != null ? v.getMake() : "");
        modelField.setText(v.getModel() != null ? v.getModel() : "");
        yearField.setText(v.getManufacturingYear() != null ? String.valueOf(v.getManufacturingYear()) : "");
    }

    @FXML
    protected void onClearClick() {
        selectedVehicle = null;
        locationIdField.clear();
        barcodeIdField.clear();
        vehicleTypeCombo.setValue(null);
        licenseNumberField.clear();
        stockNumberField.clear();
        statusCombo.setValue(null);
        makeField.clear();
        modelField.clear();
        yearField.clear();
        
        vehicleTable.getSelectionModel().clearSelection();
        deleteButton.setDisable(true);
        messageLabel.setText("Form cleared. Ready to add a new vehicle.");
        messageLabel.setStyle("-fx-text-fill: black;");
    }

    @FXML
    protected void onSaveClick() {
        try {
            boolean isNew = (selectedVehicle == null);
            Vehicle v = isNew ? new Vehicle() : selectedVehicle;
            
            v.setLocationId(Long.parseLong(locationIdField.getText()));
            v.setBarcodeId(Long.parseLong(barcodeIdField.getText()));
            v.setVehicleType(vehicleTypeCombo.getValue());
            v.setLicenseNumber(licenseNumberField.getText());
            v.setStockNumber(stockNumberField.getText());
            v.setStatus(statusCombo.getValue());
            
            v.setMake(makeField.getText().trim().isEmpty() ? null : makeField.getText().trim());
            v.setModel(modelField.getText().trim().isEmpty() ? null : modelField.getText().trim());
            v.setManufacturingYear(yearField.getText().trim().isEmpty() ? null : Integer.parseInt(yearField.getText().trim()));

            if (isNew) {
                vehicleDAO.create(v);
                messageLabel.setText("Vehicle Created Successfully! Assigned ID: " + v.getId());
            } else {
                vehicleDAO.update(v);
                messageLabel.setText("Vehicle Data Updated Successfully! ID: " + v.getId());
            }
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            
            refreshTable();
            onClearClick(); 
        } catch (NumberFormatException nfe) {
            messageLabel.setText("Validation Error: Please use numbers for Location, Barcode, and Year.");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } catch (Exception e) {
            messageLabel.setText("Error saving to database. Ensure all required fields are filled properly.");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDeleteClick() {
        if (selectedVehicle != null) {
            try {
                vehicleDAO.delete(selectedVehicle.getId());
                messageLabel.setText("Vehicle Record Deleted Successfully! (ID: " + selectedVehicle.getId() + ")");
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                refreshTable();
                onClearClick();
            } catch (Exception e) {
                messageLabel.setText("Error deleting vehicle record.");
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                e.printStackTrace();
            }
        }
    }
}
