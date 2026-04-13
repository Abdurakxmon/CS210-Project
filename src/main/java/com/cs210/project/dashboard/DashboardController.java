package com.cs210.project.dashboard;

import com.cs210.project.MainApp;
import com.cs210.project.domain.account.Account;
import com.cs210.project.domain.account.AccountManagementService;
import com.cs210.project.domain.account.AccountOperationResult;
import com.cs210.project.domain.account.AccountRole;
import com.cs210.project.domain.account.AccountSaveRequest;
import com.cs210.project.domain.account.AccountStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class DashboardController {

    private static final String AUTH_VIEW = "/com/cs210/project/auth.fxml";

    private final AccountManagementService accountManagementService = new AccountManagementService();

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label emailLabel;
    @FXML private Label statusLabel;
    @FXML private Label limitedAccessLabel;
    @FXML private Label managementMessageLabel;

    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> idColumn;
    @FXML private TableColumn<Account, String> fullNameColumn;
    @FXML private TableColumn<Account, String> emailColumn;
    @FXML private TableColumn<Account, String> roleColumn;
    @FXML private TableColumn<Account, String> statusColumn;

    @FXML private TextField accountIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<AccountRole> roleComboBox;
    @FXML private ComboBox<AccountStatus> statusComboBox;
    @FXML private PasswordField passwordField;

    private Account currentAccount;

    @FXML
    public void initialize() {
        configureTable();
        configureRoleAndStatusSelectors();
        setManagementMessage("", false);

        accountsTable.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {
            if (current != null) {
                populateForm(current);
            }
        });
    }

    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
        renderAccount();
        refreshAccounts();
    }

    @FXML
    protected void onLogoutClick() {
        try {
            MainApp.showScene(AUTH_VIEW, "CS210 Project | Authentication", 1120, 720);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    protected void onRefreshAccountsClick() {
        refreshAccounts();
    }

    @FXML
    protected void onNewAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        clearForm();
        accountsTable.getSelectionModel().clearSelection();
        setManagementMessage("Ready to create a new account.", false);
    }

    @FXML
    protected void onSaveAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        Long accountId = parseId(accountIdField.getText());
        AccountSaveRequest request = new AccountSaveRequest(
                accountId,
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                roleComboBox.getValue(),
                statusComboBox.getValue(),
                passwordField.getText()
        );

        AccountOperationResult result = accountManagementService.saveAccount(request);
        setManagementMessage(result.message(), !result.success());
        if (!result.success()) {
            return;
        }

        if (currentAccount != null && currentAccount.getId().equals(result.account().getId())) {
            currentAccount = result.account();
            renderAccount();
        }

        refreshAccounts();
        selectAccount(result.account());
        passwordField.clear();
    }

    @FXML
    protected void onDeleteAccountClick() {
        if (!isSuperAdmin()) {
            setManagementMessage("Only a super admin can manage users.", true);
            return;
        }

        Long accountId = parseId(accountIdField.getText());
        AccountOperationResult result = accountManagementService.deleteAccount(
                accountId,
                currentAccount == null ? null : currentAccount.getId()
        );
        setManagementMessage(result.message(), !result.success());
        if (!result.success()) {
            return;
        }

        clearForm();
        refreshAccounts();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getId() == null ? "-" : String.valueOf(cell.getValue().getId())
        ));
        fullNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayName()));
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(blankFallback(cell.getValue().getEmail())));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoleLabel()));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatusLabel()));
    }

    private void configureRoleAndStatusSelectors() {
        roleComboBox.setItems(FXCollections.observableArrayList(AccountRole.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(AccountStatus.values()));

        roleComboBox.setConverter(enumConverter());
        statusComboBox.setConverter(enumConverter());
    }

    private <T> StringConverter<T> enumConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    private void renderAccount() {
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

        accountsTable.setItems(FXCollections.observableArrayList(accountManagementService.findAllAccounts()));
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
        roleComboBox.setValue(AccountRole.MEMBER);
        statusComboBox.setValue(AccountStatus.ACTIVE);
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

    private void setManagementMessage(String message, boolean error) {
        managementMessageLabel.setText(message == null ? "" : message);
        managementMessageLabel.getStyleClass().removeAll("status-error", "status-success");
        if (!message.isBlank()) {
            managementMessageLabel.getStyleClass().add(error ? "status-error" : "status-success");
        }
    }

    private boolean isSuperAdmin() {
        return currentAccount != null && currentAccount.isSuperAdmin();
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
