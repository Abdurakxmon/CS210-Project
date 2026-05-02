package com.cs210.project.ui.backend;

import com.cs210.project.models.Location;
import com.cs210.project.models.ParkingStall;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.ParkingStallRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ParkingStallManagementView extends VBox {
    private final ParkingStallRepository stallRepo = new ParkingStallRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final TableView<ParkingStall> table = new TableView<>();

    public ParkingStallManagementView() {
        setPadding(new Insets(20));
        setSpacing(16);
        getStyleClass().add("backend-inventory-root");

        VBox hero = new VBox(6);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("Fleet Operations");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("Parking Stall Management");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Track stall locations, zones, and the vehicle currently occupying each space.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        // Actions
        HBox actions = new HBox(10);
        actions.getStyleClass().add("backend-action-bar");
        Button addBtn = new Button("Add Stall");
        addBtn.getStyleClass().add("backend-primary-btn");
        addBtn.setOnAction(e -> showStallDialog(null));

        Button editBtn = new Button("Edit Stall");
        editBtn.getStyleClass().add("backend-secondary-btn");
        editBtn.setOnAction(e -> {
            ParkingStall selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showStallDialog(selected);
            else new Alert(Alert.AlertType.INFORMATION, "Select a stall to edit.").show();
        });

        Button deleteBtn = new Button("Delete Stall");
        deleteBtn.getStyleClass().add("backend-danger-btn");
        deleteBtn.setOnAction(e -> {
            ParkingStall selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (!"Available".equals(selected.getOccupancyStatus())) {
                    new Alert(Alert.AlertType.ERROR, "This stall is occupied. Move or return the vehicle before deleting it.").show();
                    return;
                }
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete stall " + selected.getStallNumber() + "?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        stallRepo.delete(selected.getId());
                        loadData();
                    }
                });
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Select a stall to delete.").show();
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);

        // Table
        TableColumn<ParkingStall, String> numberCol = new TableColumn<>("Stall Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("stallNumber"));

        TableColumn<ParkingStall, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("locationName"));

        TableColumn<ParkingStall, String> idenCol = new TableColumn<>("Identifier/Zone");
        idenCol.setCellValueFactory(new PropertyValueFactory<>("locationIdentifier"));

        TableColumn<ParkingStall, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("occupancyStatus"));

        TableColumn<ParkingStall, String> vehicleCol = new TableColumn<>("Assigned Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("assignedVehicleDisplay"));

        table.getColumns().addAll(numberCol, locationCol, idenCol, statusCol, vehicleCol);
        table.setPlaceholder(new Label("No parking stalls configured."));
        table.getStyleClass().add("backend-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(hero, actions, table);
        loadData();
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(stallRepo.findAll()));
    }

    private void showStallDialog(ParkingStall stall) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(stall == null ? "Add Parking Stall" : "Edit Parking Stall");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        TextField numberField = new TextField(stall != null ? stall.getStallNumber() : "");
        TextField idenField = new TextField(stall != null ? stall.getLocationIdentifier() : "");
        ComboBox<Location> locBox = new ComboBox<>(FXCollections.observableArrayList(locationRepo.findAll()));
        numberField.getStyleClass().add("backend-text-input");
        idenField.getStyleClass().add("backend-text-input");
        locBox.getStyleClass().add("backend-input");
        
        if (stall != null) {
            locBox.getItems().stream().filter(l -> l.getId() == stall.getLocationId()).findFirst().ifPresent(locBox::setValue);
        }

        grid.add(new Label("Location:"), 0, 0); grid.add(locBox, 1, 0);
        grid.add(new Label("Stall Number:"), 0, 1); grid.add(numberField, 1, 1);
        grid.add(new Label("Zone/Identifier:"), 0, 2); grid.add(idenField, 1, 2);

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("backend-primary-btn");
        saveBtn.setOnAction(e -> {
            if (locBox.getValue() == null || numberField.getText().isBlank()) {
                new Alert(Alert.AlertType.ERROR, "Location and stall number are required.").show();
                return;
            }
            
            ParkingStall s = stall == null ? new ParkingStall() : stall;
            s.setLocationId(locBox.getValue().getId());
            s.setStallNumber(numberField.getText());
            s.setLocationIdentifier(idenField.getText());

            if (stall == null) stallRepo.create(s);
            else stallRepo.update(s);
            
            loadData();
            dialog.close();
        });

        VBox layout = new VBox(10, grid, saveBtn);
        layout.setPadding(new Insets(10));
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }
}
