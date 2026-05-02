package com.cs210.project.ui.backend;

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
import javafx.scene.layout.Priority;
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
        setSpacing(16);
        getStyleClass().add("backend-inventory-root");

        VBox hero = new VBox(6);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("Administration");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("Locations");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Manage pickup and return branches connected to each rental system.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        TableColumn<Location, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Location, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Location, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Location, String> countryCol = new TableColumn<>("Country");
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));

        table.getColumns().addAll(idCol, nameCol, cityCol, countryCol);
        table.getStyleClass().add("backend-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No locations found."));
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.getStyleClass().add("backend-action-bar");
        Button addBtn = new Button("Add Location");
        addBtn.getStyleClass().add("backend-primary-btn");
        addBtn.setOnAction(e -> showDialog(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.getStyleClass().add("backend-secondary-btn");
        editBtn.setOnAction(e -> {
            Location selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDialog(selected);
            else new Alert(Alert.AlertType.INFORMATION, "Select a location to edit.").show();
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("backend-danger-btn");
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
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Select a location to delete.").show();
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        getChildren().addAll(hero, actions, table);
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
        for (TextField field : new TextField[]{nameField, addressField, cityField, stateField, zipField, countryField}) {
            field.getStyleClass().add("backend-text-input");
        }
        
        ComboBox<RentalSystem> systemCombo = new ComboBox<>(FXCollections.observableArrayList(systemRepo.findAll()));
        systemCombo.getStyleClass().add("backend-input");
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
        saveBtn.getStyleClass().add("backend-primary-btn");
        saveBtn.setOnAction(e -> {
            if (nameField.getText() == null || nameField.getText().isBlank() || systemCombo.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Name and rental system are required.").show();
                return;
            }
            
            Location l = (loc == null) ? new Location() : loc;
            l.setName(nameField.getText().trim());
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
