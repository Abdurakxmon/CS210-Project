package com.cs210.project.ui.frontend;

import com.cs210.project.models.Account;
import com.cs210.project.models.Location;
import com.cs210.project.models.Member;
import com.cs210.project.models.Vehicle;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.MemberRepository;
import com.cs210.project.repositories.BillRepository;
import com.cs210.project.repositories.ReservationRepository;
import com.cs210.project.repositories.VehicleRepository;
import com.cs210.project.services.ReservationService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.cs210.project.constants.Enums.*;

public class ReservationView extends VBox {
    private final ReservationService resService = new ReservationService();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final Account currentUser;
    private Vehicle preSelectedVehicle;

    public ReservationView(Account currentUser) {
        this(currentUser, null);
    }

    public ReservationView(Account currentUser, Vehicle preSelectedVehicle) {
        this.currentUser = currentUser;
        this.preSelectedVehicle = preSelectedVehicle;
        setupUI();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Create Reservation");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.getItems().addAll(vehicleRepo.findAll());
        if (preSelectedVehicle != null) {
            for (Vehicle v : vehicleCombo.getItems()) {
                if (v.getId() == preSelectedVehicle.getId()) {
                    vehicleCombo.setValue(v);
                    break;
                }
            }
        }
        
        ComboBox<Location> pickupLocCombo = new ComboBox<>();
        List<Location> locations = locationRepo.findAll();
        pickupLocCombo.getItems().addAll(locations);
        
        ComboBox<Location> returnLocCombo = new ComboBox<>();
        returnLocCombo.getItems().addAll(locations);

        DatePicker pickupDatePicker = new DatePicker(java.time.LocalDate.now().plusDays(1));
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });
        pickupDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });

        ComboBox<VehicleType> typeFilter = new ComboBox<>(javafx.collections.FXCollections.observableArrayList(VehicleType.values()));
        typeFilter.setPromptText("Any type");
        ComboBox<TransmissionType> transmissionFilter = new ComboBox<>(javafx.collections.FXCollections.observableArrayList(TransmissionType.values()));
        transmissionFilter.setPromptText("Any transmission");
        ComboBox<FuelType> fuelFilter = new ComboBox<>(javafx.collections.FXCollections.observableArrayList(FuelType.values()));
        fuelFilter.setPromptText("Any fuel");
        TextField minPriceField = new TextField();
        minPriceField.setPromptText("Min price");
        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("Max price");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Min seats");
        Label statusLabel = new Label();

        Button searchVehiclesBtn = new Button("Search Available Vehicles");
        searchVehiclesBtn.setOnAction(e -> {
            if (pickupDatePicker.getValue() == null || dueDatePicker.getValue() == null) {
                statusLabel.setText("Select pickup and return dates first.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            LocalDateTime pickup = pickupDatePicker.getValue().atTime(10, 0);
            LocalDateTime due = dueDatePicker.getValue().atTime(12, 0);
            if (!due.isAfter(pickup)) {
                statusLabel.setText("Return date must be after pickup date.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            Double minPrice = parseDoubleOrNull(minPriceField.getText());
            Double maxPrice = parseDoubleOrNull(maxPriceField.getText());
            Integer capacity = parseIntOrNull(capacityField.getText());
            Integer pickupLocationId = pickupLocCombo.getValue() != null ? pickupLocCombo.getValue().getId() : null;
            vehicleCombo.getItems().setAll(vehicleRepo.searchAvailableVehicles(
                    pickupLocationId,
                    returnLocCombo.getValue() != null ? returnLocCombo.getValue().getId() : null,
                    pickup,
                    due,
                    typeFilter.getValue(),
                    minPrice,
                    maxPrice,
                    transmissionFilter.getValue(),
                    fuelFilter.getValue(),
                    capacity
            ));
            statusLabel.setText(vehicleCombo.getItems().size() + " vehicles available for the selected dates.");
            statusLabel.setStyle("-fx-text-fill: #2c3e50;");
        });

        // Auto-select pickup location when vehicle is selected
        javafx.scene.image.ImageView vehiclePreview = new javafx.scene.image.ImageView();
        vehiclePreview.setFitWidth(200);
        vehiclePreview.setFitHeight(120);
        vehiclePreview.setPreserveRatio(true);
        vehiclePreview.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-style: solid;");

        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (Location loc : pickupLocCombo.getItems()) {
                    if (loc.getId() == newVal.getLocationId()) {
                        pickupLocCombo.setValue(loc);
                        break;
                    }
                }
                if (newVal.getImagePath() != null && !newVal.getImagePath().isBlank()) {
                    try {
                        vehiclePreview.setImage(new javafx.scene.image.Image(newVal.getImagePath()));
                    } catch (Exception ex) {
                        vehiclePreview.setImage(null);
                    }
                } else {
                    vehiclePreview.setImage(null);
                }
            } else {
                vehiclePreview.setImage(null);
            }
        });

        // Trigger listener if pre-selected
        if (vehicleCombo.getValue() != null) {
            Vehicle val = vehicleCombo.getValue();
            for (Location loc : pickupLocCombo.getItems()) {
                if (loc.getId() == val.getLocationId()) {
                    pickupLocCombo.setValue(loc);
                    break;
                }
            }
            if (val.getImagePath() != null && !val.getImagePath().isBlank()) {
                try {
                    vehiclePreview.setImage(new javafx.scene.image.Image(val.getImagePath()));
                } catch (Exception ex) {}
            }
        }

        grid.add(new Label("Preview:"), 2, 0, 1, 3);
        grid.add(vehiclePreview, 2, 1, 1, 5);

        // Add-ons Section
        VBox addonsBox = new VBox(10);
        addonsBox.setPadding(new Insets(10));
        addonsBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label addonsTitle = new Label("Additional Services & Add-ons");
        addonsTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane addonsGrid = new GridPane();
        addonsGrid.setHgap(20);
        addonsGrid.setVgap(10);

        List<CheckBox> insuranceChecks = new ArrayList<>();
        int iIdx = 0;
        for (InsuranceType type : InsuranceType.values()) {
            CheckBox cb = new CheckBox(type.name());
            cb.setUserData(type);
            insuranceChecks.add(cb);
            addonsGrid.add(cb, 0, iIdx++);
        }

        List<CheckBox> equipmentChecks = new ArrayList<>();
        iIdx = 0;
        for (EquipmentType type : EquipmentType.values()) {
            CheckBox cb = new CheckBox(type.name());
            cb.setUserData(type);
            equipmentChecks.add(cb);
            addonsGrid.add(cb, 1, iIdx++);
        }

        List<CheckBox> serviceChecks = new ArrayList<>();
        iIdx = 0;
        for (ServiceType type : ServiceType.values()) {
            CheckBox cb = new CheckBox(type.name());
            cb.setUserData(type);
            serviceChecks.add(cb);
            addonsGrid.add(cb, 2, iIdx++);
        }
        addonsBox.getChildren().addAll(addonsTitle, addonsGrid);

        Button submitBtn = new Button("Confirm Reservation");

        submitBtn.setOnAction(e -> {
            try {
                Vehicle v = vehicleCombo.getValue();
                Location p = pickupLocCombo.getValue();
                Location r = returnLocCombo.getValue();
                if (v == null || p == null || r == null || pickupDatePicker.getValue() == null || dueDatePicker.getValue() == null) {
                    statusLabel.setText("Please select vehicle, locations, pickup date, and return date.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                LocalDateTime pickup = pickupDatePicker.getValue().atTime(10, 0);
                LocalDateTime due = dueDatePicker.getValue().atTime(12, 0);
                if (!due.isAfter(pickup)) {
                    statusLabel.setText("Return date must be after pickup date.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                Member member = memberRepo.findByAccountId(currentUser.getId());
                if (member == null) {
                    statusLabel.setText("Only members can create reservations.");
                    return;
                }

                List<InsuranceType> selectedInsurances = new ArrayList<>();
                for (CheckBox cb : insuranceChecks) if (cb.isSelected()) selectedInsurances.add((InsuranceType) cb.getUserData());

                List<EquipmentType> selectedEquipments = new ArrayList<>();
                for (CheckBox cb : equipmentChecks) if (cb.isSelected()) selectedEquipments.add((EquipmentType) cb.getUserData());

                List<ServiceType> selectedServices = new ArrayList<>();
                for (CheckBox cb : serviceChecks) if (cb.isSelected()) selectedServices.add((ServiceType) cb.getUserData());

                String reservationNumber = resService.createReservation(member.getId(), v.getId(), p.getId(), r.getId(), pickup, due, selectedInsurances, selectedEquipments, selectedServices);
                statusLabel.setText("Reservation created successfully!");
                BillRepository billRepo = new BillRepository();
                com.cs210.project.models.VehicleReservation created = new ReservationRepository().findByNumber(reservationNumber);
                com.cs210.project.models.Bill bill = created != null ? billRepo.findByReservationId(created.getId()) : null;
                if (bill != null) statusLabel.setText("Reservation created. Estimated bill: $" + bill.getTotalAmount());
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        grid.add(new Label("Vehicle:"), 0, 0);
        grid.add(vehicleCombo, 1, 0);
        grid.add(new Label("Pickup Location:"), 0, 1);
        grid.add(pickupLocCombo, 1, 1);
        grid.add(new Label("Return Location:"), 0, 2);
        grid.add(returnLocCombo, 1, 2);
        grid.add(new Label("Pickup Date:"), 0, 3);
        grid.add(pickupDatePicker, 1, 3);
        grid.add(new Label("Return Date:"), 0, 4);
        grid.add(dueDatePicker, 1, 4);
        grid.add(new Label("Type:"), 0, 5);
        grid.add(typeFilter, 1, 5);
        grid.add(new Label("Price Range:"), 0, 6);
        grid.add(new HBox(5, minPriceField, maxPriceField), 1, 6);
        grid.add(new Label("Transmission:"), 0, 7);
        grid.add(transmissionFilter, 1, 7);
        grid.add(new Label("Fuel:"), 0, 8);
        grid.add(fuelFilter, 1, 8);
        grid.add(new Label("Capacity:"), 0, 9);
        grid.add(capacityField, 1, 9);
        grid.add(searchVehiclesBtn, 1, 10);
        grid.add(submitBtn, 1, 11);

        getChildren().addAll(title, grid, addonsBox, statusLabel);
    }

    private Double parseDoubleOrNull(String value) {
        try {
            return value == null || value.isBlank() ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntOrNull(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
