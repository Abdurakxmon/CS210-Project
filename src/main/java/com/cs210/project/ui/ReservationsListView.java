package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.VehicleReservation;
import com.cs210.project.services.ReservationService;
import com.cs210.project.services.RentalService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReservationsListView extends VBox {
    private final ReservationService resService = new ReservationService();
    private final RentalService rentalService = new RentalService();
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

        table.getColumns().addAll(resNumCol, vehicleCol, statusCol, dueCol);

        HBox actions = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadData());

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
            if (selected != null) {
                try {
                    rentalService.returnVehicle(selected.getReservationNumber(), Session.getAccount().getId());
                    loadData();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.show();
                }
            }
        });

        actions.getChildren().addAll(refreshBtn, cancelBtn, returnBtn);
        getChildren().addAll(title, actions, table);
    }

    private void loadData() {
        List<VehicleReservation> data;
        if (Session.isMember()) {
            data = resService.getMyReservations(Session.getMember().getId());
        } else {
            data = resService.getAllReservations();
        }
        table.setItems(FXCollections.observableArrayList(data));
    }
}
