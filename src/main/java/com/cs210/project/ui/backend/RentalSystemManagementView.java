package com.cs210.project.ui.backend;

import com.cs210.project.models.RentalSystem;
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

public class RentalSystemManagementView extends VBox {
    private final RentalSystemRepository repo = new RentalSystemRepository();
    private final TableView<RentalSystem> table = new TableView<>();

    public RentalSystemManagementView() {
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
        Label title = new Label("Rental Systems");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Manage rental brands and branches used by vehicle locations.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        TableColumn<RentalSystem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<RentalSystem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(idCol, nameCol);
        table.getStyleClass().add("backend-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No rental systems found."));
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.getStyleClass().add("backend-action-bar");
        Button addBtn = new Button("Add System");
        addBtn.getStyleClass().add("backend-primary-btn");
        addBtn.setOnAction(e -> showDialog(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.getStyleClass().add("backend-secondary-btn");
        editBtn.setOnAction(e -> {
            RentalSystem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDialog(selected);
            else new Alert(Alert.AlertType.INFORMATION, "Select a system to edit.").show();
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("backend-danger-btn");
        deleteBtn.setOnAction(e -> {
            RentalSystem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        repo.delete(selected.getId());
                        loadData();
                    }
                });
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Select a system to delete.").show();
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        getChildren().addAll(hero, actions, table);
    }

    private void showDialog(RentalSystem system) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(system == null ? "Add System" : "Edit System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(system != null ? system.getName() : "");
        nameField.getStyleClass().add("backend-text-input");
        grid.add(new Label("System Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("backend-primary-btn");
        saveBtn.setOnAction(e -> {
            if (nameField.getText() == null || nameField.getText().isBlank()) {
                new Alert(Alert.AlertType.ERROR, "System name is required.").show();
                return;
            }
            if (system == null) {
                RentalSystem s = new RentalSystem();
                s.setName(nameField.getText().trim());
                repo.create(s);
            } else {
                system.setName(nameField.getText().trim());
                repo.update(system);
            }
            dialog.close();
            loadData();
        });

        grid.add(saveBtn, 1, 1);
        dialog.setScene(new Scene(grid));
        dialog.showAndWait();
    }

    private void loadData() {
        List<RentalSystem> systems = repo.findAll();
        table.setItems(FXCollections.observableArrayList(systems));
    }
}
