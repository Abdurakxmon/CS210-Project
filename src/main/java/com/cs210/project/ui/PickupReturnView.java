package com.cs210.project.ui;

import com.cs210.project.services.RentalService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.cs210.project.config.Session;

public class PickupReturnView extends VBox {
    private final RentalService rentalService = new RentalService();

    public PickupReturnView() {
        setupUI();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);

        Label title = new Label("Vehicle Pickup & Return");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField resNumberField = new TextField();
        resNumberField.setPromptText("Enter Reservation Number (e.g. RES-XXXX)");
        resNumberField.setMinWidth(300);

        HBox barcodeSearch = new HBox(10);
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Scan Vehicle Barcode...");
        Button findByBarcodeBtn = new Button("Find Reservation");
        barcodeSearch.getChildren().addAll(barcodeField, findByBarcodeBtn);

        Label msgLabel = new Label();

        findByBarcodeBtn.setOnAction(e -> {
            try {
                com.cs210.project.models.VehicleReservation res = rentalService.findReservationByBarcode(barcodeField.getText());
                resNumberField.setText(res.getReservationNumber());
                msgLabel.setText("Found Reservation: " + res.getReservationNumber());
                msgLabel.setStyle("-fx-text-fill: blue;");
            } catch (Exception ex) {
                msgLabel.setText("Barcode Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        HBox buttons = new HBox(10);
        Button pickupBtn = new Button("Pickup Vehicle");
        Button returnBtn = new Button("Return Vehicle");

        pickupBtn.setOnAction(e -> {
            try {
                rentalService.pickupVehicle(resNumberField.getText(), Session.getAccount().getId());
                msgLabel.setText("Vehicle picked up successfully!");
                msgLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                msgLabel.setText("Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        returnBtn.setOnAction(e -> {
            try {
                rentalService.returnVehicle(resNumberField.getText(), Session.getAccount().getId());
                msgLabel.setText("Vehicle returned successfully!");
                msgLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                msgLabel.setText("Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        buttons.getChildren().addAll(pickupBtn, returnBtn);
        getChildren().addAll(title, new Label("Scan Vehicle:"), barcodeSearch, new Label("Or Enter Manually:"), resNumberField, buttons, msgLabel);
    }
}
