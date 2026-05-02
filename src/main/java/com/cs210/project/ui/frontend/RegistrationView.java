package com.cs210.project.ui.frontend;

import com.cs210.project.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;

public class RegistrationView extends StackPane {
    private final AuthService authService = new AuthService();
    private final Runnable onBack;

    public RegistrationView(Runnable onBack) {
        this.onBack = onBack;
        setupUI();
    }

    private void setupUI() {
        // Background Image
        ImageView bgImage = new ImageView(new Image("https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&q=80&w=1500"));
        bgImage.setFitWidth(900);
        bgImage.setFitHeight(680);
        bgImage.setPreserveRatio(false);

        VBox darkOverlay = new VBox();
        darkOverlay.getStyleClass().add("register-bg-overlay");

        // Form Container
        VBox card = new VBox(20);
        card.getStyleClass().add("register-card");
        card.setMaxWidth(600);
        card.setMaxHeight(600);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Join the Elite");
        title.getStyleClass().add("register-title");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = createStyledTextField("Full Name");
        TextField phoneField = createStyledTextField("Phone");
        TextField emailField = createStyledTextField("Email");
        TextField addressField = createStyledTextField("Address");
        TextField cityField = createStyledTextField("City");
        TextField stateField = createStyledTextField("State");
        TextField zipcodeField = createStyledTextField("Zipcode");
        
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Birth Date");
        birthDatePicker.getStyleClass().add("luxury-text-field");
        birthDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.getStyleClass().add("luxury-text-field");
        
        TextField licenseField = createStyledTextField("Driver License #");
        DatePicker expiryPicker = new DatePicker();
        expiryPicker.setPromptText("License Expiry");
        expiryPicker.getStyleClass().add("luxury-text-field");
        expiryPicker.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        grid.add(createSectionLabel("Personal Information"), 0, r++, 2, 1);
        grid.add(nameField, 0, r); grid.add(phoneField, 1, r++);
        grid.add(emailField, 0, r); grid.add(birthDatePicker, 1, r++);
        
        grid.add(createSectionLabel("Address"), 0, r++, 2, 1);
        grid.add(addressField, 0, r, 2, 1); r++;
        grid.add(cityField, 0, r); grid.add(stateField, 1, r++);
        grid.add(zipcodeField, 0, r++, 2, 1);

        grid.add(createSectionLabel("Account & License"), 0, r++, 2, 1);
        grid.add(usernameField, 0, r); grid.add(passwordField, 1, r++);
        grid.add(licenseField, 0, r); grid.add(expiryPicker, 1, r++);

        scrollPane.setContent(grid);

        Button registerBtn = new Button("CREATE ACCOUNT");
        registerBtn.getStyleClass().addAll("premium-button", "premium-button-success");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(45);

        Button backBtn = new Button("Back to Login");
        backBtn.getStyleClass().add("luxury-link");
        backBtn.setStyle("-fx-background-color: transparent;"); // Keep transparent bg
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
            try {
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
                    msgLabel.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    msgLabel.setText("Registration failed. Username or License might already be in use.");
                    msgLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (Exception ex) {
                msgLabel.setText("System error: " + ex.getMessage());
                msgLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        card.getChildren().addAll(title, scrollPane, registerBtn, backBtn, msgLabel);
        getChildren().addAll(bgImage, darkOverlay, card);
    }

    private Label createSectionLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("register-section-label");
        return l;
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(40);
        tf.getStyleClass().add("luxury-text-field");
        return tf;
    }
}
