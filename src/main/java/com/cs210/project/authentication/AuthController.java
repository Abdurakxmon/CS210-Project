package com.cs210.project.authentication;

import com.cs210.project.MainApp;
import com.cs210.project.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class AuthController {

    private static final String DASHBOARD_VIEW = "/com/cs210/project/dashboard.fxml";
    private final AuthenticationService authenticationService = new AuthenticationService();

    @FXML private TabPane authTabPane;
    @FXML private Tab loginTab;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginMessageLabel;

    @FXML private TextField registerFullNameField;
    @FXML private TextField registerEmailField;
    @FXML private TextField registerPhoneField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label registerMessageLabel;

    @FXML
    public void initialize() {
        resetLabel(loginMessageLabel);
        resetLabel(registerMessageLabel);

        authTabPane.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {
            resetLabel(loginMessageLabel);
            resetLabel(registerMessageLabel);
        });
    }

    @FXML
    protected void onLoginClick() {
        AuthResult result = authenticationService.login(loginEmailField.getText(), loginPasswordField.getText());
        setStatus(loginMessageLabel, result.message(), !result.success());

        if (!result.success() || result.account() == null) {
            return;
        }

        try {
            DashboardController dashboardController = MainApp.showScene(
                    DASHBOARD_VIEW,
                    "CS210 Project | Dashboard",
                    980,
                    700
            );
            dashboardController.setCurrentAccount(result.account());
        } catch (Exception exception) {
            exception.printStackTrace();
            setStatus(loginMessageLabel, "Login succeeded, but the dashboard could not be opened.", true);
        }
    }

    @FXML
    protected void onRegisterClick() {
        RegistrationRequest request = new RegistrationRequest(
                registerFullNameField.getText(),
                registerEmailField.getText(),
                registerPhoneField.getText(),
                registerPasswordField.getText(),
                registerConfirmPasswordField.getText()
        );

        AuthResult result = authenticationService.register(request);
        setStatus(registerMessageLabel, result.message(), !result.success());

        if (!result.success()) {
            return;
        }

        loginEmailField.setText(request.email() == null ? "" : request.email().trim().toLowerCase());
        loginPasswordField.clear();
        clearRegistrationFields();
        authTabPane.getSelectionModel().select(loginTab);
        setStatus(loginMessageLabel, "Registration complete. Sign in with your new account.", false);
    }

    private void clearRegistrationFields() {
        registerFullNameField.clear();
        registerEmailField.clear();
        registerPhoneField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
    }

    private void resetLabel(Label label) {
        label.setText("");
        label.getStyleClass().removeAll("status-error", "status-success");
    }

    private void setStatus(Label label, String message, boolean error) {
        resetLabel(label);
        label.setText(message == null ? "" : message);
        label.getStyleClass().add(error ? "status-error" : "status-success");
    }
}
