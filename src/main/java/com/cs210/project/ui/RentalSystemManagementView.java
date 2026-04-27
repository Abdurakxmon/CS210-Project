package com.cs210.project.ui;

import com.cs210.project.models.RentalSystem;
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

public class RentalSystemManagementView extends VBox {
    private final RentalSystemRepository repo = new RentalSystemRepository();
    private final TableView<RentalSystem> table = new TableView<>();

    public RentalSystemManagementView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Rental System Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableColumn<RentalSystem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<RentalSystem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(idCol, nameCol);

        HBox actions = new HBox(10);
        Button addBtn = new Button("Add System");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showDialog(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            RentalSystem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showDialog(selected);
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
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
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        getChildren().addAll(title, actions, table);
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
        grid.add(new Label("System Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            if (system == null) {
                RentalSystem s = new RentalSystem();
                s.setName(nameField.getText());
                repo.create(s);
            } else {
                system.setName(nameField.getText());
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
