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
        setSpacing(20);

        Label title = new Label("Parking Stall Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Actions
        HBox actions = new HBox(10);
        Button addBtn = new Button("Add Stall");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showStallDialog(null));

        Button editBtn = new Button("Edit Stall");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            ParkingStall selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showStallDialog(selected);
        });

        Button deleteBtn = new Button("Delete Stall");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            ParkingStall selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stallRepo.delete(selected.getId());
                loadData();
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);

        // Table
        TableColumn<ParkingStall, String> numberCol = new TableColumn<>("Stall Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("stallNumber"));

        TableColumn<ParkingStall, String> idenCol = new TableColumn<>("Identifier/Zone");
        idenCol.setCellValueFactory(new PropertyValueFactory<>("locationIdentifier"));

        table.getColumns().addAll(numberCol, idenCol);
        table.setPlaceholder(new Label("No parking stalls configured."));

        getChildren().addAll(title, actions, table);
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
        
        if (stall != null) {
            locBox.getItems().stream().filter(l -> l.getId() == stall.getLocationId()).findFirst().ifPresent(locBox::setValue);
        }

        grid.add(new Label("Location:"), 0, 0); grid.add(locBox, 1, 0);
        grid.add(new Label("Stall Number:"), 0, 1); grid.add(numberField, 1, 1);
        grid.add(new Label("Zone/Identifier:"), 0, 2); grid.add(idenField, 1, 2);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> {
            if (locBox.getValue() == null || numberField.getText().isEmpty()) return;
            
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
