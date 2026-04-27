package com.cs210.project.ui;

import com.cs210.project.models.Location;
import com.cs210.project.models.RentalSystem;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.RentalSystemRepository;
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

public class LocationManagementView extends VBox {
    private final LocationRepository repo = new LocationRepository();
    private final RentalSystemRepository systemRepo = new RentalSystemRepository();
    private final TableView<Location> table = new TableView<>();

    public LocationManagementView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Location Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableColumn<Location, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Location, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Location, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Location, String> countryCol = new TableColumn<>("Country");
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));

        table.getColumns().addAll(idCol, nameCol, cityCol, countryCol);

        HBox actions = new HBox(10);
        Button addBtn = new Button("Add Location");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showDialog(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            Location selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDialog(selected);
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            Location selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        repo.delete(selected.getId());
                        loadData();
                    }
                });
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        getChildren().addAll(title, actions, table);
    }

    private void showDialog(Location loc) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(loc == null ? "Add Location" : "Edit Location");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(loc != null ? loc.getName() : "");
        TextField addressField = new TextField(loc != null ? loc.getStreetAddress() : "");
        TextField cityField = new TextField(loc != null ? loc.getCity() : "");
        TextField stateField = new TextField(loc != null ? loc.getState() : "");
        TextField zipField = new TextField(loc != null ? loc.getZipcode() : "");
        TextField countryField = new TextField(loc != null ? loc.getCountry() : "");
        
        ComboBox<RentalSystem> systemCombo = new ComboBox<>(FXCollections.observableArrayList(systemRepo.findAll()));
        if (loc != null) {
            for (RentalSystem s : systemCombo.getItems()) {
                if (s.getId() == loc.getSystemId()) {
                    systemCombo.setValue(s);
                    break;
                }
            }
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("City:"), 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(new Label("State:"), 0, 3);
        grid.add(stateField, 1, 3);
        grid.add(new Label("Zipcode:"), 0, 4);
        grid.add(zipField, 1, 4);
        grid.add(new Label("Country:"), 0, 5);
        grid.add(countryField, 1, 5);
        grid.add(new Label("Rental System:"), 0, 6);
        grid.add(systemCombo, 1, 6);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            if (systemCombo.getValue() == null) return;
            
            Location l = (loc == null) ? new Location() : loc;
            l.setName(nameField.getText());
            l.setStreetAddress(addressField.getText());
            l.setCity(cityField.getText());
            l.setState(stateField.getText());
            l.setZipcode(zipField.getText());
            l.setCountry(countryField.getText());
            l.setSystemId(systemCombo.getValue().getId());

            if (loc == null) repo.create(l);
            else repo.update(l);
            
            dialog.close();
            loadData();
        });

        grid.add(saveBtn, 1, 7);
        dialog.setScene(new Scene(grid));
        dialog.showAndWait();
    }

    private void loadData() {
        List<Location> locations = repo.findAll();
        table.setItems(FXCollections.observableArrayList(locations));
    }
}
