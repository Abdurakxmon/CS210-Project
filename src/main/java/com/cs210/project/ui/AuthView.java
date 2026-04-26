package com.cs210.project.ui;

import com.cs210.project.models.Account;
import com.cs210.project.services.AuthService;
import com.cs210.project.config.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class AuthView extends VBox {
    private final AuthService authService = new AuthService();
    private final Consumer<Account> onLoginSuccess;
    private final Runnable onRegisterClick;

    public AuthView(Consumer<Account> onLoginSuccess, Runnable onRegisterClick) {
        this.onLoginSuccess = onLoginSuccess;
        this.onRegisterClick = onRegisterClick;
        setupUI();
    }

    private void setupUI() {
        setPadding(new Insets(50));
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Car Rental System");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox loginBox = new VBox(15);
        loginBox.setMaxWidth(350);
        loginBox.setAlignment(Pos.CENTER);

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setPrefHeight(40);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(40);

        Button loginBtn = new Button("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(45);
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");

        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setOnAction(e -> onRegisterClick.run());

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");

        loginBtn.setOnAction(e -> {
            if (authService.login(userField.getText(), passField.getText())) {
                onLoginSuccess.accept(Session.getAccount());
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        });

        loginBox.getChildren().addAll(userField, passField, loginBtn, registerLink, errorLabel);
        getChildren().addAll(title, loginBox);
    }
}
