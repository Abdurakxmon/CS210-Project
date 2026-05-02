package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.constants.Enums.ReservationStatus;
import com.cs210.project.models.*;
import com.cs210.project.repositories.*;
import com.cs210.project.services.ReservationService;
import com.cs210.project.services.RentalService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
    private final String highlightReservationNumber;
    private final String feedbackMessage;
    private final VBox memberCards = new VBox(14);
    private final Label memberSummaryLabel = new Label();
    private final Label backendSummaryLabel = new Label();
    private List<VehicleReservation> backendReservations = List.of();
    private TextField backendSearchField;
    private ComboBox<ReservationStatus> backendStatusFilter;
    private DatePicker backendPickupFromPicker;
    private DatePicker backendPickupToPicker;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public ReservationsListView() {
        this(null, null);
    }

    public ReservationsListView(String highlightReservationNumber, String feedbackMessage) {
        this.highlightReservationNumber = highlightReservationNumber;
        this.feedbackMessage = feedbackMessage;
        setupUI();
        loadData();
    }

    private void setupUI() {
        if (Session.isMember()) {
            setupMemberReservationsUI();
            return;
        }

        getStyleClass().add("backend-reservations-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("OPERATIONS");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("All Reservations");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Search reservations, monitor pickup status, and handle cancellation or return workflows.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        backendSummaryLabel.getStyleClass().add("backend-inventory-summary");
        hero.getChildren().addAll(eyebrow, title, subtitle, backendSummaryLabel);

        Label feedbackLabel = new Label(feedbackMessage == null ? "" : feedbackMessage);
        feedbackLabel.setWrapText(true);
        feedbackLabel.setVisible(feedbackMessage != null && !feedbackMessage.isBlank());
        feedbackLabel.setManaged(feedbackLabel.isVisible());
        feedbackLabel.setStyle("-fx-background-color: #e8f7ee; -fx-text-fill: #17633a; -fx-padding: 10 12; -fx-background-radius: 8; -fx-font-weight: bold;");

        GridPane filterGrid = createBackendFilterGrid();

        // Columns
        TableColumn<VehicleReservation, String> resNumCol = new TableColumn<>("Res #");
        resNumCol.setCellValueFactory(new PropertyValueFactory<>("reservationNumber"));

        TableColumn<VehicleReservation, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));

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

        table.getColumns().addAll(resNumCol, memberCol, vehicleCol, statusCol, pickupCol, dueCol, amountCol, paidCol);
        table.getStyleClass().add("backend-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.getStyleClass().add("backend-action-bar");
        actions.setAlignment(Pos.CENTER_LEFT);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("backend-secondary-btn");
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
        copyBtn.getStyleClass().add("backend-secondary-btn");
        actions.getChildren().add(copyBtn);

        if (!Session.isMember()) {
            Button addBtn = new Button("Add Reservation");
            addBtn.getStyleClass().add("backend-primary-btn");
            addBtn.setOnAction(e -> showReservationDialog(null));

            Button editBtn = new Button("Edit Selected");
            editBtn.getStyleClass().add("backend-secondary-btn");
            editBtn.setOnAction(e -> {
                VehicleReservation selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) showReservationDialog(selected);
            });

            Button deleteBtn = new Button("Delete Selected");
            deleteBtn.getStyleClass().add("backend-danger-btn");
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
        cancelBtn.getStyleClass().add("backend-danger-btn");
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
        returnBtn.getStyleClass().add("backend-primary-btn");
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
        detailsBtn.getStyleClass().add("backend-secondary-btn");
        detailsBtn.setOnAction(e -> {
            VehicleReservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDetailsDialog(selected);
        });

        actions.getChildren().addAll(cancelBtn, returnBtn, detailsBtn);
        getChildren().addAll(hero, feedbackLabel, filterGrid, actions, table);
    }

    private GridPane createBackendFilterGrid() {
        GridPane filterGrid = new GridPane();
        filterGrid.getStyleClass().add("backend-filter-panel");
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(16));

        Label searchLabel = new Label("Search");
        searchLabel.getStyleClass().add("backend-filter-label");
        Label statusLabel = new Label("Status");
        statusLabel.getStyleClass().add("backend-filter-label");
        Label fromLabel = new Label("Pickup from");
        fromLabel.getStyleClass().add("backend-filter-label");
        Label toLabel = new Label("Pickup to");
        toLabel.getStyleClass().add("backend-filter-label");

        backendSearchField = new TextField();
        backendSearchField.setPromptText("Reservation, member, vehicle, plate...");
        backendSearchField.getStyleClass().add("backend-text-input");
        backendSearchField.setPrefWidth(280);
        backendSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyBackendReservationFilters());

        backendStatusFilter = new ComboBox<>(FXCollections.observableArrayList(ReservationStatus.values()));
        backendStatusFilter.setPromptText("Any status");
        backendStatusFilter.getStyleClass().add("backend-input");
        backendStatusFilter.setPrefWidth(160);
        backendStatusFilter.setOnAction(e -> applyBackendReservationFilters());

        backendPickupFromPicker = new DatePicker();
        backendPickupFromPicker.getStyleClass().add("backend-input");
        backendPickupFromPicker.setEditable(false);
        backendPickupFromPicker.setOnAction(e -> applyBackendReservationFilters());

        backendPickupToPicker = new DatePicker();
        backendPickupToPicker.getStyleClass().add("backend-input");
        backendPickupToPicker.setEditable(false);
        backendPickupToPicker.setOnAction(e -> applyBackendReservationFilters());

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("backend-secondary-btn");
        clearBtn.setOnAction(e -> {
            backendSearchField.clear();
            backendStatusFilter.setValue(null);
            backendPickupFromPicker.setValue(null);
            backendPickupToPicker.setValue(null);
            applyBackendReservationFilters();
        });

        filterGrid.add(searchLabel, 0, 0);
        filterGrid.add(backendSearchField, 1, 0, 2, 1);
        filterGrid.add(statusLabel, 3, 0);
        filterGrid.add(backendStatusFilter, 4, 0);
        filterGrid.add(fromLabel, 0, 1);
        filterGrid.add(backendPickupFromPicker, 1, 1);
        filterGrid.add(toLabel, 2, 1);
        filterGrid.add(backendPickupToPicker, 3, 1);
        filterGrid.add(clearBtn, 4, 1);
        return filterGrid;
    }

    private void setupMemberReservationsUI() {
        getStyleClass().add("reservations-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("reservations-hero");
        Label eyebrow = new Label("MEMBER DASHBOARD");
        eyebrow.getStyleClass().add("reservations-eyebrow");
        Label title = new Label("My Reservations");
        title.getStyleClass().add("reservations-title");
        Label subtitle = new Label("Track upcoming pickups, rental status, totals, and return actions in one place.");
        subtitle.getStyleClass().add("reservations-subtitle");
        memberSummaryLabel.getStyleClass().add("reservations-summary");
        hero.getChildren().addAll(eyebrow, title, subtitle, memberSummaryLabel);

        Label feedbackLabel = new Label(feedbackMessage == null ? "" : feedbackMessage);
        feedbackLabel.getStyleClass().add("reservations-feedback");
        feedbackLabel.setWrapText(true);
        feedbackLabel.setVisible(feedbackMessage != null && !feedbackMessage.isBlank());
        feedbackLabel.setManaged(feedbackLabel.isVisible());

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("reservations-secondary-btn");
        refreshBtn.setOnAction(e -> loadData());
        actions.getChildren().add(refreshBtn);

        memberCards.getStyleClass().add("reservations-card-list");
        ScrollPane scrollPane = new ScrollPane(memberCards);
        scrollPane.getStyleClass().add("reservations-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(hero, feedbackLabel, actions, scrollPane);
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

        VBox layout = new VBox(16);
        layout.getStyleClass().add("reservation-modal");
        layout.setPadding(new Insets(22));
        layout.setMinWidth(560);

        HBox hero = new HBox(16);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("reservation-modal-hero");
        StackPane visual = createReservationVisual(res, 190, 112);
        VBox heroText = new VBox(5);
        Label header = new Label(res.getVehicleMake() + " " + res.getVehicleModel());
        header.getStyleClass().add("reservation-modal-title");
        Label number = new Label(res.getReservationNumber());
        number.getStyleClass().add("reservation-modal-code");
        Label status = new Label(res.getStatus().getLabel());
        status.getStyleClass().addAll("reservation-status-pill", statusStyleClass(res.getStatus()));
        heroText.getChildren().addAll(header, number, status);
        hero.getChildren().addAll(visual, heroText);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("reservation-modal-grid");
        grid.setHgap(16);
        grid.setVgap(10);

        int r = 0;
        addModalRow(grid, "Member", res.getMemberName(), r++);
        addModalRow(grid, "License plate", res.getVehiclePlate(), r++);
        addModalRow(grid, "Pickup", formatDate(res.getPickupDate()), r++);
        addModalRow(grid, "Due", formatDate(res.getDueDate()), r++);
        addModalRow(grid, "Pickup location", res.getPickupLocationName() != null ? res.getPickupLocationName() : String.valueOf(res.getPickupLocationId()), r++);
        addModalRow(grid, "Return location", res.getReturnLocationName() != null ? res.getReturnLocationName() : String.valueOf(res.getReturnLocationId()), r++);
        
        Bill bill = billRepo.findByReservationId(res.getId());
        String costStr = "N/A";
        String paidStr = "N/A";
        if (bill != null) {
            costStr = String.format("$%.2f", bill.getTotalAmount());
            BigDecimal paid = paymentRepo.getSuccessfulPaidAmount(bill.getId());
            paidStr = String.format("$%.2f", paid);
        }
        addModalRow(grid, "Total cost", costStr, r++);
        addModalRow(grid, "Paid so far", paidStr, r++);

        if (res.getStatus() == ReservationStatus.COMPLETED) {
            addModalRow(grid, "Returned on", formatDate(res.getReturnDate()), r++);
            addModalRow(grid, "Processed by", res.getStaffName() != null ? res.getStaffName() : "System", r++);
        }

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("reservations-primary-btn");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(hero, grid, closeBtn);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void addModalRow(GridPane grid, String label, String value, int row) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("reservation-modal-label");
        Label valueNode = new Label(value == null || value.isBlank() ? "N/A" : value);
        valueNode.getStyleClass().add("reservation-modal-value");
        valueNode.setWrapText(true);
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
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
            table.setItems(FXCollections.observableArrayList(data));
            populateMemberCards(data);
        } else {
            data = resService.getAllReservations();
            if (Session.isWorker()) {
                // Filter for cars waiting for inspection
                data = data.stream()
                    .filter(res -> res.getStatus() == ReservationStatus.WAITING_FOR_INSPECTION)
                    .toList();
            }
            backendReservations = data;
            applyBackendReservationFilters();
        }
        selectHighlightedReservation();
    }

    private void applyBackendReservationFilters() {
        if (Session.isMember() || backendReservations == null) {
            return;
        }

        String query = backendSearchField == null || backendSearchField.getText() == null
                ? ""
                : backendSearchField.getText().trim().toLowerCase(Locale.ROOT);
        ReservationStatus status = backendStatusFilter == null ? null : backendStatusFilter.getValue();
        java.time.LocalDate pickupFrom = backendPickupFromPicker == null ? null : backendPickupFromPicker.getValue();
        java.time.LocalDate pickupTo = backendPickupToPicker == null ? null : backendPickupToPicker.getValue();

        List<VehicleReservation> filtered = backendReservations.stream()
                .filter(reservation -> matchesBackendSearch(reservation, query))
                .filter(reservation -> status == null || reservation.getStatus() == status)
                .filter(reservation -> pickupFrom == null || (reservation.getPickupDate() != null && !reservation.getPickupDate().toLocalDate().isBefore(pickupFrom)))
                .filter(reservation -> pickupTo == null || (reservation.getPickupDate() != null && !reservation.getPickupDate().toLocalDate().isAfter(pickupTo)))
                .toList();

        table.setItems(FXCollections.observableArrayList(filtered));
        updateBackendSummary(filtered);
    }

    private boolean matchesBackendSearch(VehicleReservation reservation, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String haystack = String.join(" ",
                value(reservation.getReservationNumber()),
                value(reservation.getMemberName()),
                value(reservation.getVehicleMake()),
                value(reservation.getVehicleModel()),
                value(reservation.getVehiclePlate()),
                value(reservation.getPickupLocationName()),
                value(reservation.getReturnLocationName())).toLowerCase(Locale.ROOT);
        return haystack.contains(query);
    }

    private void updateBackendSummary(List<VehicleReservation> reservations) {
        if (backendSummaryLabel == null || reservations == null) {
            return;
        }
        long confirmed = reservations.stream().filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED).count();
        long active = reservations.stream().filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING).count();
        long waitingInspection = reservations.stream().filter(reservation -> reservation.getStatus() == ReservationStatus.WAITING_FOR_INSPECTION).count();
        backendSummaryLabel.setText(reservations.size() + " reservations · " + confirmed + " confirmed · "
                + active + " active · " + waitingInspection + " waiting inspection");
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void populateMemberCards(List<VehicleReservation> reservations) {
        memberCards.getChildren().clear();
        int activeCount = (int) reservations.stream()
                .filter(res -> res.getStatus() != ReservationStatus.CANCELLED && res.getStatus() != ReservationStatus.COMPLETED)
                .count();
        memberSummaryLabel.setText(reservations.size() + (reservations.size() == 1 ? " reservation" : " reservations")
                + " · " + activeCount + " active");

        if (reservations.isEmpty()) {
            VBox empty = new VBox(8);
            empty.getStyleClass().add("reservations-empty");
            empty.setAlignment(Pos.CENTER);
            Label emptyTitle = new Label("No reservations yet");
            emptyTitle.getStyleClass().add("reservations-empty-title");
            Label emptyBody = new Label("Choose an available automobile and confirm your pickup details to create one.");
            emptyBody.getStyleClass().add("reservations-empty-body");
            empty.getChildren().addAll(emptyTitle, emptyBody);
            memberCards.getChildren().add(empty);
            return;
        }

        for (VehicleReservation reservation : reservations) {
            memberCards.getChildren().add(createMemberReservationCard(reservation));
        }
    }

    private VBox createMemberReservationCard(VehicleReservation reservation) {
        VBox card = new VBox(12);
        card.getStyleClass().add("reservation-card");
        if (highlightReservationNumber != null && highlightReservationNumber.equals(reservation.getReservationNumber())) {
            card.getStyleClass().add("reservation-card-highlight");
        }

        HBox header = new HBox(14);
        header.setAlignment(Pos.TOP_LEFT);
        StackPane visual = createReservationVisual(reservation, 150, 88);
        VBox titleBlock = new VBox(4);
        Label vehicle = new Label(reservation.getVehicleMake() + " " + reservation.getVehicleModel());
        vehicle.getStyleClass().add("reservation-card-title");
        Label number = new Label(reservation.getReservationNumber());
        number.getStyleClass().add("reservation-card-number");
        titleBlock.getChildren().addAll(vehicle, number);
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Label status = new Label(reservation.getStatus() == null ? "Status" : reservation.getStatus().getLabel());
        status.getStyleClass().addAll("reservation-status-pill", statusStyleClass(reservation.getStatus()));
        header.getChildren().addAll(visual, titleBlock, headerSpacer, status);

        HBox details = new HBox(10);
        details.getStyleClass().add("reservation-detail-grid");
        details.getChildren().addAll(
                createDetailBlock("Pickup", formatDate(reservation.getPickupDate())),
                createDetailBlock("Return", formatDate(reservation.getDueDate())),
                createDetailBlock("Location", reservation.getPickupLocationName() != null ? reservation.getPickupLocationName() : "Assigned"),
                createDetailBlock("Total", String.format("$%.2f", reservation.getAmount())),
                createDetailBlock("Paid", String.format("$%.2f", reservation.getPaidAmount()))
        );

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button copyBtn = new Button("Copy #");
        copyBtn.getStyleClass().add("reservations-secondary-btn");
        copyBtn.setOnAction(e -> copyReservationNumber(reservation));
        Button detailsBtn = new Button("View details");
        detailsBtn.getStyleClass().add("reservations-secondary-btn");
        detailsBtn.setOnAction(e -> showDetailsDialog(reservation));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("reservations-danger-btn");
        cancelBtn.setDisable(reservation.getStatus() != ReservationStatus.CONFIRMED);
        cancelBtn.setOnAction(e -> cancelReservation(reservation));
        Button returnBtn = new Button("Return");
        returnBtn.getStyleClass().add("reservations-primary-btn");
        returnBtn.setDisable(reservation.getStatus() != ReservationStatus.PENDING);
        returnBtn.setOnAction(e -> initiateMemberReturn(reservation));
        actions.getChildren().addAll(copyBtn, detailsBtn, cancelBtn, returnBtn);

        card.getChildren().addAll(header, details, actions);
        return card;
    }

    private StackPane createReservationVisual(VehicleReservation reservation, double width, double height) {
        StackPane visual = new StackPane();
        visual.getStyleClass().add("reservation-visual");
        visual.setPrefSize(width, height);
        visual.setMinSize(width, height);
        visual.setMaxSize(width, height);

        if (reservation.getVehicleImagePath() != null && !reservation.getVehicleImagePath().isBlank()) {
            try {
                ImageView imageView = new ImageView(new Image(reservation.getVehicleImagePath(), width, height, true, true, true));
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                visual.getChildren().add(imageView);
                return visual;
            } catch (Exception ignored) {
                // Use branded placeholder below.
            }
        }

        VBox placeholder = new VBox(2);
        placeholder.setAlignment(Pos.CENTER);
        Label make = new Label(reservation.getVehicleMake() == null ? "Rental" : reservation.getVehicleMake());
        make.getStyleClass().add("reservation-visual-make");
        Label model = new Label(reservation.getVehicleModel() == null ? "Vehicle" : reservation.getVehicleModel());
        model.getStyleClass().add("reservation-visual-model");
        placeholder.getChildren().addAll(make, model);
        visual.getChildren().add(placeholder);
        return visual;
    }

    private VBox createDetailBlock(String label, String value) {
        VBox block = new VBox(4);
        block.getStyleClass().add("reservation-detail-block");
        HBox.setHgrow(block, Priority.ALWAYS);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("reservation-detail-label");
        Label valueNode = new Label(value == null || value.isBlank() ? "N/A" : value);
        valueNode.getStyleClass().add("reservation-detail-value");
        valueNode.setWrapText(true);
        block.getChildren().addAll(labelNode, valueNode);
        return block;
    }

    private String formatDate(LocalDateTime date) {
        return date == null ? "N/A" : date.format(DATE_FORMAT);
    }

    private String statusStyleClass(ReservationStatus status) {
        if (status == ReservationStatus.CONFIRMED || status == ReservationStatus.WAITING_FOR_INSPECTION) {
            return "reservation-status-active";
        }
        if (status == ReservationStatus.COMPLETED) {
            return "reservation-status-complete";
        }
        if (status == ReservationStatus.CANCELLED) {
            return "reservation-status-cancelled";
        }
        return "reservation-status-neutral";
    }

    private void copyReservationNumber(VehicleReservation reservation) {
        ClipboardContent content = new ClipboardContent();
        content.putString(reservation.getReservationNumber());
        Clipboard.getSystemClipboard().setContent(content);
        showCopyDialog(reservation);
    }

    private void showCopyDialog(VehicleReservation reservation) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Reservation copied");

        VBox root = new VBox(14);
        root.getStyleClass().add("reservation-modal");
        root.setPadding(new Insets(22));
        root.setPrefWidth(380);

        Label title = new Label("Reservation number copied");
        title.getStyleClass().add("reservation-modal-title");
        Label number = new Label(reservation.getReservationNumber());
        number.getStyleClass().add("reservation-modal-code");
        Label body = new Label(reservation.getVehicleMake() + " " + reservation.getVehicleModel() + " is ready to reference at pickup or support.");
        body.getStyleClass().add("reservation-modal-body");
        body.setWrapText(true);
        Button close = new Button("Close");
        close.getStyleClass().add("reservations-primary-btn");
        close.setOnAction(e -> dialog.close());

        root.getChildren().addAll(title, number, body, close);
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private void cancelReservation(VehicleReservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel reservation " + reservation.getReservationNumber() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    resService.cancelReservation(reservation.getId());
                    loadData();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                }
            }
        });
    }

    private void initiateMemberReturn(VehicleReservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Initiate return for vehicle " + reservation.getVehiclePlate() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    rentalService.initiateReturn(reservation.getReservationNumber());
                    loadData();
                    new Alert(Alert.AlertType.INFORMATION, "Return initiated. A worker will inspect the vehicle shortly.").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                }
            }
        });
    }

    private void selectHighlightedReservation() {
        if (highlightReservationNumber == null || highlightReservationNumber.isBlank()) {
            return;
        }
        for (VehicleReservation reservation : table.getItems()) {
            if (highlightReservationNumber.equals(reservation.getReservationNumber())) {
                table.getSelectionModel().select(reservation);
                table.scrollTo(reservation);
                break;
            }
        }
    }
}
