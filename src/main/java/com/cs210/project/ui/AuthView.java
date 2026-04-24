package com.cs210.project.ui;

import com.cs210.project.controllers.AuthController;
import com.cs210.project.models.Account;
import com.cs210.project.models.AuthResult;
import com.cs210.project.models.RegistrationRequest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public final class AuthView extends VBox {

    private final AuthController authController;
    private final Consumer<Account> loginSuccessHandler;

    private final TabPane tabPane = new TabPane();
    private final Tab loginTab = new Tab("Login");

    private final TextField loginEmailField = new TextField();
    private final PasswordField loginPasswordField = new PasswordField();
    private final Label loginMessageLabel = new Label();

    private final TextField registerFullNameField = new TextField();
    private final TextField registerEmailField = new TextField();
    private final TextField registerPhoneField = new TextField();
    private final PasswordField registerPasswordField = new PasswordField();
    private final PasswordField registerConfirmPasswordField = new PasswordField();
    private final Label registerMessageLabel = new Label();

    public AuthView(AuthController authController, Consumer<Account> loginSuccessHandler) {
        this.authController = authController;
        this.loginSuccessHandler = loginSuccessHandler;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(24));
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f5f7fb, #e9eef5);");

        VBox card = new VBox(12);
        card.setMaxWidth(560);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 16; " +
                        "-fx-border-radius: 16; " +
                        "-fx-border-color: #d7deea;"
        );

        Label title = new Label("CS210 Car Rental");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 700;");

        Label subtitle = new Label("Sign in or create a new account");
        subtitle.setStyle("-fx-text-fill: #556070;");

        configureTabs();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        card.getChildren().addAll(title, subtitle, tabPane);

        HBox center = new HBox(card);
        center.setAlignment(Pos.CENTER);
        getChildren().add(center);
    }

    private void configureTabs() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(loginTab, createRegisterTab());
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            clearMessage(loginMessageLabel);
            clearMessage(registerMessageLabel);
        });

        loginTab.setContent(createLoginContent());
    }

    private VBox createLoginContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(14, 6, 6, 6));

        styleField(loginEmailField, "you@example.com");
        styleField(loginPasswordField, "Password");

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #1f6f8b; -fx-text-fill: white; -fx-font-weight: 700;");
        loginButton.setOnAction(event -> onLoginClick());

        loginMessageLabel.setMinHeight(32);
        loginMessageLabel.setWrapText(true);

        content.getChildren().addAll(
                new Label("Email"), loginEmailField,
                new Label("Password"), loginPasswordField,
                loginButton, loginMessageLabel
        );
        return content;
    }

    private Tab createRegisterTab() {
        Tab registerTab = new Tab("Register");

        VBox content = new VBox(10);
        content.setPadding(new Insets(14, 6, 6, 6));

        styleField(registerFullNameField, "Your full name");
        styleField(registerEmailField, "you@example.com");
        styleField(registerPhoneField, "+1 555 123 4567");
        styleField(registerPasswordField, "Minimum 6 characters");
        styleField(registerConfirmPasswordField, "Confirm password");

        Button registerButton = new Button("Create account");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: #2f855a; -fx-text-fill: white; -fx-font-weight: 700;");
        registerButton.setOnAction(event -> onRegisterClick());

        registerMessageLabel.setMinHeight(32);
        registerMessageLabel.setWrapText(true);

        content.getChildren().addAll(
                new Label("Full name"), registerFullNameField,
                new Label("Email"), registerEmailField,
                new Label("Phone"), registerPhoneField,
                new Label("Password"), registerPasswordField,
                new Label("Confirm password"), registerConfirmPasswordField,
                registerButton, registerMessageLabel
        );

        registerTab.setContent(content);
        return registerTab;
    }

    private void onLoginClick() {
        AuthResult result = authController.login(loginEmailField.getText(), loginPasswordField.getText());
        setMessage(loginMessageLabel, result.message(), !result.success());
        if (result.success() && result.account() != null) {
            loginSuccessHandler.accept(result.account());
        }
    }

    private void onRegisterClick() {
        RegistrationRequest request = new RegistrationRequest(
                registerFullNameField.getText(),
                registerEmailField.getText(),
                registerPhoneField.getText(),
                registerPasswordField.getText(),
                registerConfirmPasswordField.getText()
        );

        AuthResult result = authController.register(request);
        setMessage(registerMessageLabel, result.message(), !result.success());

        if (!result.success()) {
            return;
        }

        loginEmailField.setText(request.email() == null ? "" : request.email().trim().toLowerCase());
        loginPasswordField.clear();
        clearRegistrationFields();
        tabPane.getSelectionModel().select(loginTab);
        setMessage(loginMessageLabel, "Registration complete. Sign in with your new account.", false);
    }

    private void clearRegistrationFields() {
        registerFullNameField.clear();
        registerEmailField.clear();
        registerPhoneField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
    }

    private void styleField(TextField field, String promptText) {
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d3dce8; -fx-border-radius: 8;");
    }

    private void setMessage(Label label, String message, boolean error) {
        label.setText(message == null ? "" : message);
        label.setStyle(error
                ? "-fx-text-fill: #b91c1c; -fx-font-weight: 700;"
                : "-fx-text-fill: #0f766e; -fx-font-weight: 700;");
    }

    private void clearMessage(Label label) {
        label.setText("");
        label.setStyle("");
    }
}
