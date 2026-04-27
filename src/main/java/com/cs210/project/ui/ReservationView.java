package com.cs210.project.ui;

import com.cs210.project.models.Account;
import com.cs210.project.models.Location;
import com.cs210.project.models.Member;
import com.cs210.project.models.Vehicle;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.MemberRepository;
import com.cs210.project.repositories.VehicleRepository;
import com.cs210.project.services.ReservationService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });

        // Auto-select pickup location when vehicle is selected
        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (Location loc : pickupLocCombo.getItems()) {
                    if (loc.getId() == newVal.getLocationId()) {
                        pickupLocCombo.setValue(loc);
                        break;
                    }
                }
            }
        });

        // Trigger listener if pre-selected
        if (vehicleCombo.getValue() != null) {
            for (Location loc : pickupLocCombo.getItems()) {
                if (loc.getId() == vehicleCombo.getValue().getLocationId()) {
                    pickupLocCombo.setValue(loc);
                    break;
                }
            }
        }

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
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            try {
                Vehicle v = vehicleCombo.getValue();
                Location p = pickupLocCombo.getValue();
                Location r = returnLocCombo.getValue();
                LocalDateTime due = dueDatePicker.getValue().atTime(12, 0);

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

                resService.createReservation(member.getId(), v.getId(), p.getId(), r.getId(), due, selectedInsurances, selectedEquipments, selectedServices);
                statusLabel.setText("Reservation created successfully!");
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
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);
        grid.add(submitBtn, 1, 4);

        getChildren().addAll(title, grid, addonsBox, statusLabel);
    }
}
