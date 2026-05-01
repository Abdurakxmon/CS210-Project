package com.cs210.project.ui;

import com.cs210.project.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class RegistrationView extends VBox {
    private final AuthService authService = new AuthService();
    private final Runnable onBack;

    public RegistrationView(Runnable onBack) {
        this.onBack = onBack;
        setupUI();
    }

    private void setupUI() {
        setPadding(new Insets(40));
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #f4f4f4;");

        Label title = new Label("Member Registration");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField stateField = new TextField();
        TextField zipcodeField = new TextField();
        DatePicker birthDatePicker = new DatePicker();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField licenseField = new TextField();
        DatePicker expiryPicker = new DatePicker();

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("City:"), 0, 4);
        grid.add(cityField, 1, 4);
        grid.add(new Label("State:"), 0, 5);
        grid.add(stateField, 1, 5);
        grid.add(new Label("Zipcode:"), 0, 6);
        grid.add(zipcodeField, 1, 6);
        grid.add(new Label("Birth Date:"), 0, 7);
        grid.add(birthDatePicker, 1, 7);
        grid.add(new Label("Username:"), 0, 8);
        grid.add(usernameField, 1, 8);
        grid.add(new Label("Password:"), 0, 9);
        grid.add(passwordField, 1, 9);
        grid.add(new Label("Driver License:"), 0, 10);
        grid.add(licenseField, 1, 10);
        grid.add(new Label("License Expiry:"), 0, 11);
        grid.add(expiryPicker, 1, 11);

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        registerBtn.setMinWidth(200);

        Button backBtn = new Button("Back to Login");
        backBtn.setOnAction(e -> onBack.run());

        Label msgLabel = new Label();

        registerBtn.setOnAction(e -> {
            if (nameField.getText().isBlank() || emailField.getText().isBlank() ||
                    usernameField.getText().isBlank() || passwordField.getText().isBlank() ||
                    licenseField.getText().isBlank() || expiryPicker.getValue() == null ||
                    birthDatePicker.getValue() == null) {
                msgLabel.setText("Please fill all required fields.");
                msgLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            boolean success = authService.register(
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    zipcodeField.getText(),
                    birthDatePicker.getValue(),
                    usernameField.getText(),
                    passwordField.getText(),
                    licenseField.getText(),
                    expiryPicker.getValue().atStartOfDay()
            );
            if (success) {
                msgLabel.setText("Registration successful! Please login.");
                msgLabel.setStyle("-fx-text-fill: green;");
            } else {
                msgLabel.setText("Registration failed. Username or License might exist.");
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        getChildren().addAll(title, grid, registerBtn, backBtn, msgLabel);
    }
}
