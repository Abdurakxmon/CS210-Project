package com.cs210.project.ui.backend;

import com.cs210.project.services.RentalService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.cs210.project.config.Session;
import javafx.scene.control.TextInputDialog;

import com.cs210.project.repositories.*;
import com.cs210.project.models.*;
import com.cs210.project.constants.Enums.PaymentType;
import javafx.scene.control.*;
import java.math.BigDecimal;

public class PickupReturnView extends VBox {
    private final RentalService rentalService = new RentalService();
    private final ReservationRepository resRepo = new ReservationRepository();
    private final BillRepository billRepo = new BillRepository();
    private final PaymentRepository paymentRepo = new PaymentRepository();

    public PickupReturnView() {
        setupUI();
    }

    private void showPaymentDialog(Bill bill) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Process Payment");
        dialog.setHeaderText("Bill Details for Reservation");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMinWidth(400);

        // Bill Items List
        VBox itemsBox = new VBox(5);
        itemsBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #ddd;");
        Label itemsTitle = new Label("Items Breakdown:");
        itemsTitle.setStyle("-fx-font-weight: bold;");
        itemsBox.getChildren().add(itemsTitle);

        for (BillItem item : bill.getItems()) {
            HBox row = new HBox();
            Label name = new Label(item.getServiceName());
            Label price = new Label(String.format("$%.2f", item.getAmount()));
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            row.getChildren().addAll(name, spacer, price);
            itemsBox.getChildren().add(row);
        }

        Separator sep = new Separator();
        Label totalLbl = new Label("Grand Total: $" + bill.getTotalAmount());
        totalLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        BigDecimal paid = paymentRepo.getSuccessfulPaidAmount(bill.getId());
        BigDecimal remaining = bill.getTotalAmount().subtract(paid);
        
        Label summary = new Label(String.format("Already Paid: $%.2f | Balance: $%.2f", paid, remaining));
        summary.setStyle("-fx-text-fill: #27ae60;");

        TextField amountField = new TextField(remaining.toString());
        ComboBox<PaymentType> typeCombo = new ComboBox<>(javafx.collections.FXCollections.observableArrayList(PaymentType.values()));
        typeCombo.setValue(PaymentType.CREDIT_CARD);
        
        VBox payControls = new VBox(10, new Label("Amount to Pay:"), amountField, new Label("Payment Type:"), typeCombo);
        payControls.setPadding(new Insets(10, 0, 0, 0));

        layout.getChildren().addAll(itemsBox, sep, totalLbl, summary, payControls);
        
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    paymentRepo.processPayment(bill.getId(), new BigDecimal(amountField.getText()), typeCombo.getValue());
                    new Alert(Alert.AlertType.INFORMATION, "Payment processed successfully!").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Payment failed: " + ex.getMessage()).show();
                }
            }
        });
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
            TextInputDialog mileageDialog = new TextInputDialog("0");
            mileageDialog.setTitle("Return Vehicle");
            mileageDialog.setHeaderText("Enter Current Mileage and Condition");
            mileageDialog.setContentText("Current Mileage:");
            
            mileageDialog.showAndWait().ifPresent(mileage -> {
                TextInputDialog conditionDialog = new TextInputDialog("Good");
                conditionDialog.setTitle("Return Vehicle");
                conditionDialog.setHeaderText("Vehicle Condition");
                conditionDialog.setContentText("Notes:");
                
                conditionDialog.showAndWait().ifPresent(condition -> {
                    TextInputDialog fineDialog = new TextInputDialog("0.00");
                    fineDialog.setTitle("Return Vehicle");
                    fineDialog.setHeaderText("Assess Fines (Damage, Fuel, etc.)");
                    fineDialog.setContentText("Fine Amount:");
                    
                    fineDialog.showAndWait().ifPresent(fineStr -> {
                        try {
                            BigDecimal fineVal = new BigDecimal(fineStr);
                            rentalService.returnVehicle(resNumberField.getText(), Session.getAccount().getId(), Integer.parseInt(mileage), condition, fineVal);
                            msgLabel.setText("Vehicle returned successfully!");
                            msgLabel.setStyle("-fx-text-fill: green;");
                        } catch (Exception ex) {
                            msgLabel.setText("Error: " + ex.getMessage());
                            msgLabel.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
            });
        });

        Button payBtn = new Button("Pay Bill");
        payBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        payBtn.setOnAction(e -> {
            try {
                VehicleReservation res = resRepo.findByNumber(resNumberField.getText());
                if (res == null) throw new Exception("Reservation not found.");
                Bill bill = billRepo.findByReservationId(res.getId());
                if (bill == null) throw new Exception("No bill found for this reservation.");
                
                showPaymentDialog(bill);
            } catch (Exception ex) {
                msgLabel.setText("Payment Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        buttons.getChildren().addAll(pickupBtn, payBtn, returnBtn);
        getChildren().addAll(title, new Label("Scan Vehicle:"), barcodeSearch, new Label("Or Enter Manually:"), resNumberField, buttons, msgLabel);
    }
}
