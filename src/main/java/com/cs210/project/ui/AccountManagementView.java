package com.cs210.project.ui;

import com.cs210.project.constants.Enums.*;
import com.cs210.project.models.Account;
import com.cs210.project.models.Person;
import com.cs210.project.repositories.AccountRepository;
import com.cs210.project.repositories.MemberRepository;
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

public class AccountManagementView extends VBox {
    private final AccountRepository accountRepo = new AccountRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final TableView<Account> table = new TableView<>();

    public AccountManagementView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Account Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableColumn<Account, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Account, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPerson().getName()));

        TableColumn<Account, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Account, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roleType"));

        TableColumn<Account, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Account, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        table.getColumns().addAll(idCol, nameCol, userCol, roleCol, statusCol, activeCol);

        HBox actions = new HBox(10);
        Button addBtn = new Button("Add User");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showUserDialog(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            Account selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showUserDialog(selected);
        });

        Button deactivateBtn = new Button("Deactivate Selected");
        deactivateBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deactivateBtn.setOnAction(e -> {
            Account selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deactivate " + selected.getUsername() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        accountRepo.deactivate(selected.getId());
                        loadData();
                    }
                });
            }
        });

        Button reactivateBtn = new Button("Reactivate Selected");
        reactivateBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        reactivateBtn.setOnAction(e -> {
            Account selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                accountRepo.reactivate(selected.getId());
                loadData();
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deactivateBtn, reactivateBtn);
        getChildren().addAll(title, actions, table);
    }

    private void showUserDialog(Account account) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(account == null ? "Add User" : "Edit User");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(account != null ? account.getPerson().getName() : "");
        TextField emailField = new TextField(account != null ? account.getPerson().getEmail() : "");
        TextField phoneField = new TextField(account != null ? account.getPerson().getPhone() : "");
        TextField addressField = new TextField(account != null ? account.getPerson().getStreetAddress() : "");
        TextField cityField = new TextField(account != null ? account.getPerson().getCity() : "");
        DatePicker birthDatePicker = new DatePicker(account != null ? account.getPerson().getBirthDate() : null);
        TextField userField = new TextField(account != null ? account.getUsername() : "");
        PasswordField passField = new PasswordField();
        TextField licenseField = new TextField();
        DatePicker licenseExpiryPicker = new DatePicker(java.time.LocalDate.now().plusYears(3));
        ComboBox<RoleType> roleCombo = new ComboBox<>(FXCollections.observableArrayList(RoleType.values()));
        roleCombo.setValue(account != null ? account.getRoleType() : RoleType.MEMBER);
        ComboBox<AccountStatus> statusCombo = new ComboBox<>(FXCollections.observableArrayList(AccountStatus.values()));
        statusCombo.setValue(account != null ? account.getStatus() : AccountStatus.ACTIVE);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("City:"), 0, 4);
        grid.add(cityField, 1, 4);
        grid.add(new Label("Birth Date:"), 0, 5);
        grid.add(birthDatePicker, 1, 5);
        grid.add(new Label("Username:"), 0, 6);
        grid.add(userField, 1, 6);
        
        if (account == null) {
            grid.add(new Label("Password:"), 0, 7);
            grid.add(passField, 1, 7);
            grid.add(new Label("Member License:"), 0, 10);
            grid.add(licenseField, 1, 10);
            grid.add(new Label("License Expiry:"), 0, 11);
            grid.add(licenseExpiryPicker, 1, 11);
        }

        grid.add(new Label("Role:"), 0, 8);
        grid.add(roleCombo, 1, 8);
        grid.add(new Label("Status:"), 0, 9);
        grid.add(statusCombo, 1, 9);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            if (account == null) {
                Person p = new Person();
                p.setName(nameField.getText());
                p.setEmail(emailField.getText());
                p.setPhone(phoneField.getText());
                p.setStreetAddress(addressField.getText());
                p.setCity(cityField.getText());
                p.setBirthDate(birthDatePicker.getValue());
                if (roleCombo.getValue() == RoleType.MEMBER) {
                    memberRepo.register(p, userField.getText(), passField.getText(), licenseField.getText(), licenseExpiryPicker.getValue().atStartOfDay());
                } else {
                    accountRepo.create(p, userField.getText(), passField.getText(), roleCombo.getValue(), statusCombo.getValue());
                }
            } else {
                account.setUsername(userField.getText());
                account.setRoleType(roleCombo.getValue());
                account.setStatus(statusCombo.getValue());
                account.getPerson().setName(nameField.getText());
                account.getPerson().setEmail(emailField.getText());
                account.getPerson().setPhone(phoneField.getText());
                account.getPerson().setStreetAddress(addressField.getText());
                account.getPerson().setCity(cityField.getText());
                account.getPerson().setBirthDate(birthDatePicker.getValue());
                accountRepo.update(account);
            }
            dialog.close();
            loadData();
        });

        grid.add(saveBtn, 1, 12);

        dialog.setScene(new Scene(grid));
        dialog.showAndWait();
    }

    private void loadData() {
        List<Account> accounts = accountRepo.findAll();
        table.setItems(FXCollections.observableArrayList(accounts));
    }
}
