package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.constants.Enums.ReservationStatus;
import com.cs210.project.models.*;
import com.cs210.project.repositories.*;
import com.cs210.project.services.ReservationService;
import com.cs210.project.services.RentalService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public class ReservationsListView extends VBox {
    private final ReservationService resService = new ReservationService();
    private final RentalService rentalService = new RentalService();
    private final MemberRepository memberRepo = new MemberRepository();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final ParkingStallRepository stallRepo = new ParkingStallRepository();
    private final BillRepository billRepo = new BillRepository();
    private final PaymentRepository paymentRepo = new PaymentRepository();
    
    private final TableView<VehicleReservation> table = new TableView<>();

    public ReservationsListView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label(Session.isMember() ? "My Reservations" : "All Reservations");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Columns
        TableColumn<VehicleReservation, String> resNumCol = new TableColumn<>("Res #");
        resNumCol.setCellValueFactory(new PropertyValueFactory<>("reservationNumber"));

        TableColumn<VehicleReservation, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getVehicleMake() + " " + data.getValue().getVehicleModel()));

        TableColumn<VehicleReservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<VehicleReservation, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        TableColumn<VehicleReservation, String> pickupCol = new TableColumn<>("Pickup Date");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));
        TableColumn<VehicleReservation, Double> amountCol = new TableColumn<>("Total");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<VehicleReservation, Double> paidCol = new TableColumn<>("Paid");
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));

        table.getColumns().addAll(resNumCol, vehicleCol, statusCol, pickupCol, dueCol, amountCol, paidCol);

        HBox actions = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadData());

        actions.getChildren().add(refreshBtn);

        Button copyBtn = new Button("Copy Res #");
        copyBtn.setOnAction(e -> {
            VehicleReservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(selected.getReservationNumber());
                clipboard.setContent(content);
                new Alert(Alert.AlertType.INFORMATION, "Reservation number " + selected.getReservationNumber() + " copied to clipboard!").show();
            }
        });
        actions.getChildren().add(copyBtn);

        if (!Session.isMember()) {
            Button addBtn = new Button("Add Reservation");
            addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            addBtn.setOnAction(e -> showReservationDialog(null));

            Button editBtn = new Button("Edit Selected");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            editBtn.setOnAction(e -> {
                VehicleReservation selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) showReservationDialog(selected);
            });

            Button deleteBtn = new Button("Delete Selected");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> {
                VehicleReservation selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete reservation " + selected.getReservationNumber() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                resService.deleteReservation(selected.getId());
                                loadData();
                            } catch (Exception ex) {
                                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                            }
                        }
                    });
                }
            });

            actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        }

        Button cancelBtn = new Button("Cancel Selected");
        cancelBtn.setOnAction(e -> {
            VehicleReservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    resService.cancelReservation(selected.getId());
                    loadData();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.show();
                }
            }
        });

        Button returnBtn = new Button("Return Selected");
        returnBtn.setOnAction(e -> {
            VehicleReservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (Session.isMember()) {
                // One-click return for members
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Initiate return for vehicle " + selected.getVehiclePlate() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            rentalService.initiateReturn(selected.getReservationNumber());
                            loadData();
                            new Alert(Alert.AlertType.INFORMATION, "Return initiated. A worker will inspect the vehicle shortly.").show();
                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                        }
                    }
                });
            } else {
                // Worker inspection flow
                TextInputDialog mileageDialog = new TextInputDialog("0");
                mileageDialog.setTitle("Return Vehicle");
                mileageDialog.setHeaderText("Enter Current Mileage and Condition");
                mileageDialog.setContentText("Current Mileage:");
                
                mileageDialog.showAndWait().ifPresent(mileage -> showWorkerInspectionDialog(selected, mileage));
            }
        });

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        detailsBtn.setOnAction(e -> {
            VehicleReservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDetailsDialog(selected);
        });

        actions.getChildren().addAll(cancelBtn, returnBtn, detailsBtn);
        getChildren().addAll(title, actions, table);
    }

    private void showReservationDialog(VehicleReservation res) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(res == null ? "Add Reservation" : "Edit Reservation");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Member> memberCombo = new ComboBox<>(FXCollections.observableArrayList(memberRepo.findAllMembers()));
        ComboBox<Vehicle> vehicleCombo = new ComboBox<>(FXCollections.observableArrayList(vehicleRepo.findAll()));
        ComboBox<Location> pickupCombo = new ComboBox<>(FXCollections.observableArrayList(locationRepo.findAll()));
        ComboBox<Location> returnCombo = new ComboBox<>(FXCollections.observableArrayList(locationRepo.findAll()));
        DatePicker dueDatePicker = new DatePicker();
        DatePicker pickupDatePicker = new DatePicker();
        ComboBox<ReservationStatus> statusCombo = new ComboBox<>(FXCollections.observableArrayList(ReservationStatus.values()));

        if (res != null) {
            // Set initial values for editing
            for (Member m : memberCombo.getItems()) if (m.getId() == res.getMemberId()) { memberCombo.setValue(m); break; }
            for (Vehicle v : vehicleCombo.getItems()) if (v.getId() == res.getVehicleId()) { vehicleCombo.setValue(v); break; }
            for (Location l : pickupCombo.getItems()) if (l.getId() == res.getPickupLocationId()) { pickupCombo.setValue(l); break; }
            for (Location l : returnCombo.getItems()) if (l.getId() == res.getReturnLocationId()) { returnCombo.setValue(l); break; }
            pickupDatePicker.setValue(res.getPickupDate() != null ? res.getPickupDate().toLocalDate() : res.getCreationDate().toLocalDate());
            dueDatePicker.setValue(res.getDueDate().toLocalDate());
            statusCombo.setValue(res.getStatus());
        } else {
            statusCombo.setValue(ReservationStatus.CONFIRMED);
            pickupDatePicker.setValue(java.time.LocalDate.now().plusDays(1));
        }

        grid.add(new Label("Member:"), 0, 0);
        grid.add(memberCombo, 1, 0);
        grid.add(new Label("Vehicle:"), 0, 1);
        grid.add(vehicleCombo, 1, 1);
        grid.add(new Label("Pickup Loc:"), 0, 2);
        grid.add(pickupCombo, 1, 2);
        grid.add(new Label("Return Loc:"), 0, 3);
        grid.add(returnCombo, 1, 3);
        grid.add(new Label("Pickup Date:"), 0, 4);
        grid.add(pickupDatePicker, 1, 4);
        grid.add(new Label("Due Date:"), 0, 5);
        grid.add(dueDatePicker, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (memberCombo.getValue() == null || vehicleCombo.getValue() == null || 
                    pickupCombo.getValue() == null || returnCombo.getValue() == null || pickupDatePicker.getValue() == null || dueDatePicker.getValue() == null) {
                    throw new Exception("Please fill all fields.");
                }

                if (res == null) {
                    resService.createReservation(
                        memberCombo.getValue().getId(),
                        vehicleCombo.getValue().getId(),
                        pickupCombo.getValue().getId(),
                        returnCombo.getValue().getId(),
                        pickupDatePicker.getValue().atTime(10, 0),
                        dueDatePicker.getValue().atTime(12, 0),
                        new java.util.ArrayList<>(),
                        new java.util.ArrayList<>(),
                        new java.util.ArrayList<>()
                    );
                } else {
                    res.setMemberId(memberCombo.getValue().getId());
                    res.setVehicleId(vehicleCombo.getValue().getId());
                    res.setPickupLocationId(pickupCombo.getValue().getId());
                    res.setReturnLocationId(returnCombo.getValue().getId());
                    res.setPickupDate(pickupDatePicker.getValue().atTime(10, 0));
                    res.setDueDate(dueDatePicker.getValue().atTime(12, 0));
                    res.setStatus(statusCombo.getValue());
                    resService.updateReservation(res);
                }
                dialog.close();
                loadData();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });

        grid.add(saveBtn, 1, 7);
        dialog.setScene(new Scene(grid));
        dialog.showAndWait();
    }

    private void showDetailsDialog(VehicleReservation res) {
        Stage dialog = new Stage();
        dialog.setTitle("Reservation Details - " + res.getReservationNumber());
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMinWidth(400);

        Label header = new Label("Reservation Info");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        int r = 0;
        grid.add(new Label("Member:"), 0, r); grid.add(new Label(res.getMemberName()), 1, r++);
        grid.add(new Label("Vehicle:"), 0, r); grid.add(new Label(res.getVehicleMake() + " " + res.getVehicleModel() + " [" + res.getVehiclePlate() + "]"), 1, r++);
        grid.add(new Label("Status:"), 0, r); grid.add(new Label(res.getStatus().getLabel()), 1, r++);
        grid.add(new Label("Pickup:"), 0, r); grid.add(new Label(res.getPickupDate() != null ? res.getPickupDate().toString() : "N/A"), 1, r++);
        grid.add(new Label("Due:"), 0, r); grid.add(new Label(res.getDueDate() != null ? res.getDueDate().toString() : "N/A"), 1, r++);
        grid.add(new Label("Pickup Location:"), 0, r); grid.add(new Label(res.getPickupLocationName() != null ? res.getPickupLocationName() : String.valueOf(res.getPickupLocationId())), 1, r++);
        grid.add(new Label("Return Location:"), 0, r); grid.add(new Label(res.getReturnLocationName() != null ? res.getReturnLocationName() : String.valueOf(res.getReturnLocationId())), 1, r++);
        
        Bill bill = billRepo.findByReservationId(res.getId());
        String costStr = "N/A";
        String paidStr = "N/A";
        if (bill != null) {
            costStr = String.format("$%.2f", bill.getTotalAmount());
            BigDecimal paid = paymentRepo.getSuccessfulPaidAmount(bill.getId());
            paidStr = String.format("$%.2f", paid);
        }
        grid.add(new Label("Total Cost:"), 0, r); grid.add(new Label(costStr), 1, r++);
        grid.add(new Label("Paid So Far:"), 0, r); grid.add(new Label(paidStr), 1, r++);

        if (res.getStatus() == ReservationStatus.COMPLETED) {
            grid.add(new Label("Returned On:"), 0, r); grid.add(new Label(res.getReturnDate().toString()), 1, r++);
            grid.add(new Label("Processed By:"), 0, r); grid.add(new Label(res.getStaffName() != null ? res.getStaffName() : "System"), 1, r++);
        }

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(header, new Separator(), grid, new Separator(), closeBtn);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void showWorkerInspectionDialog(VehicleReservation reservation, String mileageValue) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Return Inspection");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField mileageField = new TextField(mileageValue);
        TextField fuelLevelField = new TextField("100");
        TextArea damageNotes = new TextArea();
        damageNotes.setPrefRowCount(3);
        TextField damageFeeField = new TextField("0.00");
        TextField fuelFeeField = new TextField("0.00");
        CheckBox cleanedCheck = new CheckBox("Cleaned");
        cleanedCheck.setSelected(true);
        CheckBox maintenanceCheck = new CheckBox("Maintenance Required");
        ComboBox<ParkingStall> stallCombo = new ComboBox<>(FXCollections.observableArrayList(
                stallRepo.findAvailableStalls(reservation.getReturnLocationId())));
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);

        int row = 0;
        grid.add(new Label("Mileage:"), 0, row); grid.add(mileageField, 1, row++);
        grid.add(new Label("Fuel Level %:"), 0, row); grid.add(fuelLevelField, 1, row++);
        grid.add(new Label("Damage Notes:"), 0, row); grid.add(damageNotes, 1, row++);
        grid.add(new Label("Damage Fee:"), 0, row); grid.add(damageFeeField, 1, row++);
        grid.add(new Label("Fuel Fee:"), 0, row); grid.add(fuelFeeField, 1, row++);
        grid.add(cleanedCheck, 1, row++);
        grid.add(maintenanceCheck, 1, row++);
        grid.add(new Label("Parking Stall:"), 0, row); grid.add(stallCombo, 1, row++);
        grid.add(new Label("Notes:"), 0, row); grid.add(notesArea, 1, row++);

        Button saveBtn = new Button("Complete Inspection");
        saveBtn.setOnAction(e -> {
            try {
                rentalService.returnVehicle(
                        reservation.getReservationNumber(),
                        Session.getAccount().getId(),
                        Integer.parseInt(mileageField.getText()),
                        Integer.parseInt(fuelLevelField.getText()),
                        damageNotes.getText(),
                        new BigDecimal(damageFeeField.getText()),
                        new BigDecimal(fuelFeeField.getText()),
                        cleanedCheck.isSelected(),
                        maintenanceCheck.isSelected(),
                        stallCombo.getValue() != null ? stallCombo.getValue().getId() : null,
                        notesArea.getText());
                dialog.close();
                loadData();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });

        VBox root = new VBox(10, grid, saveBtn);
        root.setPadding(new Insets(10));
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void loadData() {
        List<VehicleReservation> data;
        if (Session.isMember()) {
            data = resService.getMyReservations(Session.getMember().getId());
        } else {
            data = resService.getAllReservations();
            if (Session.isWorker()) {
                // Filter for cars waiting for inspection
                data = data.stream()
                    .filter(res -> res.getStatus() == ReservationStatus.WAITING_FOR_INSPECTION)
                    .toList();
            }
        }
        table.setItems(FXCollections.observableArrayList(data));
    }
}
