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
import java.util.List;

public class ReservationView extends VBox {
    private final ReservationService resService = new ReservationService();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final Account currentUser;

    public ReservationView(Account currentUser) {
        this.currentUser = currentUser;
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
        
        ComboBox<Location> pickupLocCombo = new ComboBox<>();
        List<Location> locations = locationRepo.findAll();
        pickupLocCombo.getItems().addAll(locations);
        
        ComboBox<Location> returnLocCombo = new ComboBox<>();
        returnLocCombo.getItems().addAll(locations);

        DatePicker dueDatePicker = new DatePicker();

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

                resService.createReservation(member.getId(), v.getId(), p.getId(), r.getId(), due);
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

        getChildren().addAll(title, grid, statusLabel);
    }
}
