package com.cs210.project.ui.backend;

import com.cs210.project.services.RentalService;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import com.cs210.project.config.Session;

import com.cs210.project.repositories.*;
import com.cs210.project.models.*;
import com.cs210.project.constants.Enums.PaymentType;
import com.cs210.project.constants.Enums.ReservationStatus;
import javafx.scene.control.*;
import java.math.BigDecimal;

public class PickupReturnView extends VBox {
    private final RentalService rentalService = new RentalService();
    private final ReservationRepository resRepo = new ReservationRepository();
    private final PaymentRepository paymentRepo = new PaymentRepository();
    private final ParkingStallRepository stallRepo = new ParkingStallRepository();
    private VehicleReservation selectedReservation;
    private Label reservationValue;
    private Label customerValue;
    private Label vehicleValue;
    private Label statusValue;
    private Label routeValue;
    private Label billValue;
    private Label msgLabel;
    private Button payBtn;
    private Button pickupBtn;
    private Button returnBtn;

    public PickupReturnView() {
        setupUI();
    }

    private boolean showPaymentDialog(Bill bill) {
        final boolean[] processed = {false};
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
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new Exception("Payment amount must be greater than $0.00.");
                    }
                    if (amount.compareTo(remaining) > 0) {
                        throw new Exception(String.format("Payment cannot exceed remaining balance of $%.2f.", remaining));
                    }
                    paymentRepo.processPayment(bill.getId(), amount, typeCombo.getValue(), Session.getAccount().getId());
                    processed[0] = true;
                    new Alert(Alert.AlertType.INFORMATION, "Payment processed successfully!").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Payment failed: " + ex.getMessage()).show();
                }
            }
        });
        return processed[0];
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(16);
        getStyleClass().add("backend-reservations-root");

        VBox hero = new VBox(6);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("Reception Desk");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("Vehicle Pickup & Return");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Verify payment before pickup, scan reservations, and complete vehicle return inspections.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        TextField resNumberField = new TextField();
        resNumberField.setPromptText("Enter Reservation Number (e.g. RES-XXXX)");
        resNumberField.setMinWidth(320);
        resNumberField.getStyleClass().add("backend-text-input");

        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Scan Vehicle Barcode...");
        barcodeField.getStyleClass().add("backend-text-input");
        Button findVehicleBtn = new Button("Find Vehicle");
        findVehicleBtn.getStyleClass().add("backend-primary-btn");

        msgLabel = new Label();
        msgLabel.setWrapText(true);

        HBox scanRow = new HBox(10, barcodeField);
        scanRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(barcodeField, Priority.ALWAYS);

        HBox reservationRow = new HBox(10, resNumberField, findVehicleBtn);
        reservationRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(resNumberField, Priority.ALWAYS);

        findVehicleBtn.setOnAction(e -> findVehicle(resNumberField.getText(), barcodeField.getText(), resNumberField));

        HBox buttons = new HBox(10);
        buttons.getStyleClass().add("backend-action-bar");
        payBtn = new Button("1. Pay Bill");
        payBtn.getStyleClass().add("backend-primary-btn");
        pickupBtn = new Button("2. Pickup Vehicle");
        pickupBtn.getStyleClass().add("backend-primary-btn");
        returnBtn = new Button("Ask Worker Inspection");
        returnBtn.getStyleClass().add("backend-secondary-btn");
        payBtn.setDisable(true);
        pickupBtn.setDisable(true);
        returnBtn.setDisable(true);

        pickupBtn.setOnAction(e -> {
            if (!confirmAction("Confirm Pickup", "Pick up this vehicle now?")) {
                return;
            }
            try {
                if (selectedReservation == null) throw new Exception("Find a vehicle first.");
                rentalService.pickupVehicle(selectedReservation.getReservationNumber(), Session.getAccount().getId());
                msgLabel.setText("Vehicle picked up successfully!");
                msgLabel.setStyle("-fx-text-fill: green;");
                refreshSelectedReservation();
            } catch (Exception ex) {
                msgLabel.setText("Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        returnBtn.setOnAction(e -> {
            if (!confirmAction("Confirm Worker Inspection", "Send this returned vehicle to the worker for inspection?")) {
                return;
            }
            try {
                if (selectedReservation == null) throw new Exception("Find a vehicle first.");
                rentalService.initiateReturn(selectedReservation.getReservationNumber());
                msgLabel.setText("Worker inspection requested. The worker can now complete return mileage, fuel, fees, and stall placement.");
                msgLabel.setStyle("-fx-text-fill: green;");
                refreshSelectedReservation();
            } catch (Exception ex) {
                msgLabel.setText("Return Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        payBtn.setOnAction(e -> {
            try {
                if (selectedReservation == null) {
                    findVehicle(resNumberField.getText(), barcodeField.getText(), resNumberField);
                    if (selectedReservation == null) return;
                }
                Bill bill = rentalService.prepareBillForPayment(selectedReservation.getReservationNumber());
                if (showPaymentDialog(bill)) {
                    msgLabel.setText("Payment recorded. Pickup is ready when the balance is fully paid.");
                    msgLabel.setStyle("-fx-text-fill: green;");
                }
                refreshSelectedReservation();
            } catch (Exception ex) {
                msgLabel.setText("Payment Error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        buttons.getChildren().addAll(payBtn, pickupBtn, returnBtn);

        VBox lookupPanel = new VBox(10);
        lookupPanel.getStyleClass().add("backend-filter-panel");
        lookupPanel.setPadding(new Insets(18));
        Label scanLabel = new Label("Scan Vehicle");
        scanLabel.getStyleClass().add("backend-filter-label");
        Label manualLabel = new Label("Reservation Number");
        manualLabel.getStyleClass().add("backend-filter-label");
        lookupPanel.getChildren().addAll(scanLabel, scanRow, manualLabel, reservationRow);

        GridPane detailGrid = new GridPane();
        detailGrid.getStyleClass().add("backend-filter-panel");
        detailGrid.setPadding(new Insets(18));
        detailGrid.setHgap(18);
        detailGrid.setVgap(10);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(130);
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        valueColumn.setFillWidth(true);
        detailGrid.getColumnConstraints().addAll(labelColumn, valueColumn);
        reservationValue = addDetailRow(detailGrid, "Reservation", "No vehicle selected", 0);
        customerValue = addDetailRow(detailGrid, "Customer", "-", 1);
        vehicleValue = addDetailRow(detailGrid, "Vehicle", "-", 2);
        statusValue = addDetailRow(detailGrid, "Status", "-", 3);
        routeValue = addDetailRow(detailGrid, "Pickup / Return", "-", 4);
        billValue = addDetailRow(detailGrid, "Bill", "-", 5);

        VBox statusPanel = new VBox(8);
        statusPanel.getStyleClass().add("backend-filter-panel");
        statusPanel.setPadding(new Insets(18));
        Label actionTitle = new Label("Payment First, Then Pickup");
        actionTitle.getStyleClass().add("backend-filter-label");
        statusPanel.getChildren().addAll(actionTitle, buttons, msgLabel);

        getChildren().addAll(hero, lookupPanel, detailGrid, statusPanel);
    }

    private Label addDetailRow(GridPane grid, String label, String value, int row) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("backend-filter-label");
        Label valueNode = new Label(value);
        valueNode.setWrapText(true);
        valueNode.setMaxWidth(Double.MAX_VALUE);
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
        return valueNode;
    }

    private void findVehicle(String reservationNumber, String barcode, TextField reservationField) {
        try {
            if (barcode != null && !barcode.isBlank()) {
                selectedReservation = rentalService.findReservationByBarcode(barcode.trim());
            } else if (reservationNumber != null && !reservationNumber.isBlank()) {
                selectedReservation = resRepo.findByNumber(reservationNumber.trim());
                if (selectedReservation == null) throw new Exception("Reservation not found.");
            } else {
                throw new Exception("Enter a reservation number or scan a vehicle barcode.");
            }

            reservationField.setText(selectedReservation.getReservationNumber());
            rentalService.prepareBillForPayment(selectedReservation.getReservationNumber());
            refreshSelectedReservation();
            msgLabel.setText("Vehicle found. Collect the bill before pickup.");
            msgLabel.setStyle("-fx-text-fill: #2563eb;");
        } catch (Exception ex) {
            selectedReservation = null;
            updateSelectedReservation(null, null);
            msgLabel.setText("Find Vehicle Error: " + ex.getMessage());
            msgLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void refreshSelectedReservation() {
        if (selectedReservation == null) return;
        try {
            selectedReservation = resRepo.findByNumber(selectedReservation.getReservationNumber());
            Bill bill = rentalService.prepareBillForPayment(selectedReservation.getReservationNumber());
            updateSelectedReservation(selectedReservation, bill);
        } catch (Exception ex) {
            msgLabel.setText("Refresh Error: " + ex.getMessage());
            msgLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void updateSelectedReservation(VehicleReservation reservation, Bill bill) {
        if (reservation == null) {
            reservationValue.setText("No vehicle selected");
            customerValue.setText("-");
            vehicleValue.setText("-");
            statusValue.setText("-");
            routeValue.setText("-");
            billValue.setText("-");
            payBtn.setDisable(true);
            pickupBtn.setDisable(true);
            returnBtn.setDisable(true);
            return;
        }

        BigDecimal total = bill != null ? bill.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paid = bill != null ? paymentRepo.getSuccessfulPaidAmount(bill.getId()) : BigDecimal.ZERO;
        BigDecimal balance = total.subtract(paid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) balance = BigDecimal.ZERO;

        reservationValue.setText(reservation.getReservationNumber());
        customerValue.setText(blankToDash(reservation.getMemberName()) + " | Driver License: " +
                blankToDash(reservation.getMemberDriverLicenseNumber()));
        vehicleValue.setText(blankToDash(reservation.getVehicleMake() + " " + reservation.getVehicleModel()) + " | " + blankToDash(reservation.getVehiclePlate()));
        statusValue.setText(reservation.getStatus() != null ? reservation.getStatus().getLabel() : "-");
        routeValue.setText(blankToDash(reservation.getPickupLocationName()) + " -> " + blankToDash(reservation.getReturnLocationName()));
        billValue.setText(String.format("Total: $%.2f | Paid: $%.2f | Balance: $%.2f", total, paid, balance));

        boolean fullyPaid = balance.compareTo(BigDecimal.ZERO) == 0;
        payBtn.setDisable(false);
        pickupBtn.setDisable(!fullyPaid);
        boolean canAskWorker = reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.OVERDUE;
        returnBtn.setDisable(!canAskWorker);
    }

    private String blankToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private boolean confirmAction(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirm.setTitle(title);
        confirm.setHeaderText(title);
        return confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showReturnDialog(String reservationNumber, Label msgLabel) {
        try {
            VehicleReservation reservation = resRepo.findByNumber(reservationNumber);
            if (reservation == null) throw new Exception("Reservation not found.");

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Return Vehicle");
            dialog.setHeaderText("Complete return inspection for " + reservation.getReservationNumber());

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(20));
            grid.setHgap(12);
            grid.setVgap(10);

            TextField mileageField = new TextField("0");
            TextField fuelLevelField = new TextField("100");
            TextArea conditionArea = new TextArea("Good");
            conditionArea.setPrefRowCount(3);
            TextField damageFeeField = new TextField("0.00");
            TextField fuelFeeField = new TextField("0.00");
            CheckBox cleanedCheck = new CheckBox("Cleaned");
            cleanedCheck.setSelected(true);
            CheckBox maintenanceCheck = new CheckBox("Maintenance Required");
            ComboBox<ParkingStall> stallCombo = new ComboBox<>(FXCollections.observableArrayList(
                    stallRepo.findAvailableStalls(reservation.getReturnLocationId())));
            stallCombo.setPromptText("Select return stall");
            TextArea notesArea = new TextArea();
            notesArea.setPrefRowCount(2);

            int row = 0;
            grid.add(new Label("Mileage:"), 0, row); grid.add(mileageField, 1, row++);
            grid.add(new Label("Fuel Level %:"), 0, row); grid.add(fuelLevelField, 1, row++);
            grid.add(new Label("Condition Notes:"), 0, row); grid.add(conditionArea, 1, row++);
            grid.add(new Label("Damage Fee:"), 0, row); grid.add(damageFeeField, 1, row++);
            grid.add(new Label("Fuel Fee:"), 0, row); grid.add(fuelFeeField, 1, row++);
            grid.add(cleanedCheck, 1, row++);
            grid.add(maintenanceCheck, 1, row++);
            grid.add(new Label("Parking Stall:"), 0, row); grid.add(stallCombo, 1, row++);
            grid.add(new Label("Notes:"), 0, row); grid.add(notesArea, 1, row);

            if (stallCombo.getItems().isEmpty()) {
                Label noStalls = new Label("No free stalls at the return location. The vehicle will be returned without a stall.");
                noStalls.setStyle("-fx-text-fill: #b45309;");
                grid.add(noStalls, 1, ++row);
            }

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        rentalService.returnVehicle(
                                reservation.getReservationNumber(),
                                Session.getAccount().getId(),
                                Integer.parseInt(mileageField.getText()),
                                Integer.parseInt(fuelLevelField.getText()),
                                conditionArea.getText(),
                                new BigDecimal(damageFeeField.getText()),
                                new BigDecimal(fuelFeeField.getText()),
                                cleanedCheck.isSelected(),
                                maintenanceCheck.isSelected(),
                                stallCombo.getValue() != null ? stallCombo.getValue().getId() : null,
                                notesArea.getText());
                        msgLabel.setText("Vehicle returned successfully!");
                        msgLabel.setStyle("-fx-text-fill: green;");
                    } catch (Exception ex) {
                        msgLabel.setText("Error: " + ex.getMessage());
                        msgLabel.setStyle("-fx-text-fill: red;");
                    }
                }
            });
        } catch (Exception ex) {
            msgLabel.setText("Error: " + ex.getMessage());
            msgLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
