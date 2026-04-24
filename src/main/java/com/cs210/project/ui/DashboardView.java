package com.cs210.project.ui;

import com.cs210.project.controllers.AccountController;
import com.cs210.project.models.Account;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class DashboardView extends VBox {

    private final Runnable logoutHandler;
    private final AccountController accountController = new AccountController();
    private Account currentAccount;

    private final Label welcomeLabel = new Label("Welcome");
    private final Label roleLabel = new Label("-");
    private final Label emailLabel = new Label("-");
    private final Label statusLabel = new Label("-");
    private final Label limitedAccessLabel = new Label("Only super admins can create, update, or delete accounts.");
    private final Label managementMessageLabel = new Label();

    private final TableView<Account> accountsTable = new TableView<>();

    private final TextField accountIdField = new TextField();
    private final TextField fullNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();
    private final ComboBox<Account.Role> roleComboBox = new ComboBox<>();
    private final ComboBox<Account.Status> statusComboBox = new ComboBox<>();
    private final PasswordField passwordField = new PasswordField();

    public DashboardView(Account currentAccount, Runnable logoutHandler) {
        this.currentAccount = currentAccount;
        this.logoutHandler = logoutHandler;
        buildView();
        clearForm();
        renderCurrentAccount();
        refreshAccounts();
    }

    private void buildView() {
        setPadding(new Insets(24));
        setSpacing(16);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f3f6fb, #e8eef6);");

        getChildren().add(createTopBar());
        getChildren().add(createProfileCard());
//        getChildren().add(createManagementCard());
    }

    private HBox createTopBar() {
        Label brandLabel = new Label("CS210 Car Rental");
        brandLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 700;");

        Label subtitleLabel = new Label("Simple dashboard");
        subtitleLabel.setStyle("-fx-text-fill: #5f6978;");

        VBox titleBox = new VBox(2, brandLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> logoutHandler.run());
        logoutButton.setStyle("-fx-background-color: #1f4f7a; -fx-text-fill: white; -fx-font-weight: 700;");

        HBox topBar = new HBox(12, titleBox, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14));
        topBar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");
        return topBar;
    }

    private VBox createProfileCard() {
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");

        limitedAccessLabel.setWrapText(true);
        limitedAccessLabel.setStyle("-fx-text-fill: #9a3412; -fx-font-weight: 700;");

        VBox profileCard = new VBox(
                8,
                welcomeLabel,
                infoRow("Role", roleLabel),
                infoRow("Email", emailLabel),
                infoRow("Status", statusLabel),
                limitedAccessLabel
        );
        profileCard.setPadding(new Insets(16));
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");
        return profileCard;
    }

    private VBox createManagementCard() {
        Label title = new Label("Account Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshAccounts());

        HBox header = new HBox(10, title, new Region(), refreshButton);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        configureTable();

        VBox editor = createEditorCard();

        VBox container = new VBox(12, header, accountsTable, editor);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");
        VBox.setVgrow(accountsTable, Priority.ALWAYS);
        return container;
    }

    private VBox createEditorCard() {
        Label editorTitle = new Label("Editor");
        editorTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 700;");

        accountIdField.setEditable(false);
        styleField(accountIdField, "Auto generated");
        styleField(fullNameField, "Full name");
        styleField(emailField, "user@example.com");
        styleField(phoneField, "+1 555 123 4567");
        styleField(passwordField, "Required for create, optional for update");

        roleComboBox.setItems(FXCollections.observableArrayList(Account.Role.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(Account.Status.values()));
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        statusComboBox.setMaxWidth(Double.MAX_VALUE);

        Button newButton = new Button("New");
        newButton.setOnAction(event -> onNewAccountClick());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> onSaveAccountClick());
        saveButton.setStyle("-fx-background-color: #1f6f8b; -fx-text-fill: white; -fx-font-weight: 700;");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> onDeleteAccountClick());
        deleteButton.setStyle("-fx-background-color: #b91c1c; -fx-text-fill: white; -fx-font-weight: 700;");

        HBox buttonRow = new HBox(8, newButton, saveButton, deleteButton);
        managementMessageLabel.setMinHeight(32);
        managementMessageLabel.setWrapText(true);

        VBox editor = new VBox(
                8,
                editorTitle,
                new Label("Account ID"), accountIdField,
                new Label("Full name"), fullNameField,
                new Label("Email"), emailField,
                new Label("Phone"), phoneField,
                new Label("Role"), roleComboBox,
                new Label("Status"), statusComboBox,
                new Label("Password"), passwordField,
                buttonRow,
                managementMessageLabel
        );
        editor.setPadding(new Insets(14));
        editor.setStyle("-fx-background-color: #f8fafd; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        return editor;
    }

    private void configureTable() {
        TableColumn<Account, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getId() == null ? "-" : String.valueOf(cell.getValue().getId())
        ));

        TableColumn<Account, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayName()));

        TableColumn<Account, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(blankFallback(cell.getValue().getEmail())));

        TableColumn<Account, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoleLabel()));

        TableColumn<Account, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatusLabel()));

        accountsTable.getColumns().setAll(idColumn, fullNameColumn, emailColumn, roleColumn, statusColumn);
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        accountsTable.setPrefHeight(260);
        accountsTable.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {
            if (current != null) {
                populateForm(current);
            }
        });
    }

    private void onNewAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        clearForm();
        accountsTable.getSelectionModel().clearSelection();
        setManagementMessage("Ready to create a new account.", false);
    }

    private void onSaveAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        AccountController.OperationResult result = accountController.saveAccount(
                parseId(accountIdField.getText()),
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                roleComboBox.getValue(),
                statusComboBox.getValue(),
                passwordField.getText()
        );
        setManagementMessage(result.message(), !result.success());
        if (!result.success()) {
            return;
        }

        Account account = result.account();

        if (currentAccount != null && currentAccount.getId() != null && currentAccount.getId().equals(account.getId())) {
            currentAccount = account;
            renderCurrentAccount();
        }

        refreshAccounts();
        selectAccount(account);
        passwordField.clear();
    }

    private void onDeleteAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        AccountController.OperationResult result = accountController.deleteAccount(
                parseId(accountIdField.getText()),
                currentAccount == null ? null : currentAccount.getId()
        );
        setManagementMessage(result.message(), !result.success());
        if (!result.success()) {
            return;
        }

        clearForm();
        refreshAccounts();
    }

    private void renderCurrentAccount() {
        if (currentAccount == null) {
            welcomeLabel.setText("Welcome");
            roleLabel.setText("-");
            emailLabel.setText("-");
            statusLabel.setText("-");
            limitedAccessLabel.setVisible(false);
            limitedAccessLabel.setManaged(false);
            accountsTable.setDisable(true);
            disableForm(true);
            return;
        }

        welcomeLabel.setText("Welcome, " + currentAccount.getDisplayName());
        roleLabel.setText(currentAccount.getRoleLabel());
        emailLabel.setText(blankFallback(currentAccount.getEmail()));
        statusLabel.setText(currentAccount.getStatusLabel());

        boolean superAdmin = isSuperAdmin();
        limitedAccessLabel.setVisible(!superAdmin);
        limitedAccessLabel.setManaged(!superAdmin);
        accountsTable.setDisable(!superAdmin);
        disableForm(!superAdmin);
        if (!superAdmin) {
            setManagementMessage("User CRUD is available only for the super admin role.", true);
        }
    }

    private void refreshAccounts() {
        if (!isSuperAdmin()) {
            accountsTable.getItems().clear();
            return;
        }
        accountsTable.setItems(FXCollections.observableArrayList(accountController.findAllAccounts()));
    }

    private void populateForm(Account account) {
        accountIdField.setText(account.getId() == null ? "" : String.valueOf(account.getId()));
        fullNameField.setText(blankForEdit(account.getFullName()));
        emailField.setText(blankForEdit(account.getEmail()));
        phoneField.setText(blankForEdit(account.getPhone()));
        roleComboBox.setValue(account.getRole());
        statusComboBox.setValue(account.getStatus());
        passwordField.clear();
    }

    private void clearForm() {
        accountIdField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.setValue(Account.Role.MEMBER);
        statusComboBox.setValue(Account.Status.ACTIVE);
        passwordField.clear();
    }

    private void selectAccount(Account account) {
        if (account == null) {
            return;
        }

        for (Account row : accountsTable.getItems()) {
            if (row.getId() != null && row.getId().equals(account.getId())) {
                accountsTable.getSelectionModel().select(row);
                accountsTable.scrollTo(row);
                populateForm(row);
                return;
            }
        }
    }

    private void disableForm(boolean disabled) {
        accountIdField.setDisable(true);
        fullNameField.setDisable(disabled);
        emailField.setDisable(disabled);
        phoneField.setDisable(disabled);
        roleComboBox.setDisable(disabled);
        statusComboBox.setDisable(disabled);
        passwordField.setDisable(disabled);
    }

    private boolean isSuperAdmin() {
        return currentAccount != null && currentAccount.isSuperAdmin();
    }

    private void setManagementMessage(String message, boolean error) {
        managementMessageLabel.setText(message == null ? "" : message);
        managementMessageLabel.setStyle(error
                ? "-fx-text-fill: #b91c1c; -fx-font-weight: 700;"
                : "-fx-text-fill: #0f766e; -fx-font-weight: 700;");
    }

    private HBox infoRow(String key, Label valueLabel) {
        Label keyLabel = new Label(key + ":");
        keyLabel.setMinWidth(72);
        keyLabel.setStyle("-fx-font-weight: 700; -fx-text-fill: #5f6978;");
        return new HBox(8, keyLabel, valueLabel);
    }

    private void styleField(Control control, String promptText) {
        if (control instanceof TextField textField) {
            textField.setPromptText(promptText);
        }
        control.setStyle("-fx-background-color: white; -fx-border-color: #d3dce8; -fx-border-radius: 8;");
    }

    private Long parseId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String blankFallback(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String blankForEdit(String value) {
        return value == null ? "" : value;
    }
}
