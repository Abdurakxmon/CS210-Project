package com.cs210.project.ui.frontend;

import com.cs210.project.models.Account;
import com.cs210.project.services.AuthService;
import com.cs210.project.config.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class AuthView extends HBox {
    private final AuthService authService = new AuthService();
    private final Consumer<Account> onLoginSuccess;
    private final Runnable onRegisterClick;

    public AuthView(Consumer<Account> onLoginSuccess, Runnable onRegisterClick) {
        this.onLoginSuccess = onLoginSuccess;
        this.onRegisterClick = onRegisterClick;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("auth-container");

        // Left Side - Decorative Image
        VBox leftPane = new VBox();
        leftPane.getStyleClass().add("auth-left-pane");
        leftPane.setPrefWidth(450);
        leftPane.setAlignment(Pos.CENTER);

        StackPane imageStack = new StackPane();
        ImageView bgImage = new ImageView(new Image("https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=1000"));
        bgImage.setFitWidth(450);
        bgImage.setFitHeight(680);
        bgImage.setPreserveRatio(false);

        VBox overlay = new VBox(10);
        overlay.getStyleClass().add("auth-overlay");
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setPadding(new Insets(40));
        
        Label brandName = new Label("CS210 LUXURY");
        brandName.getStyleClass().add("auth-brand-name");
        Label slogan = new Label("Experience the art of driving.");
        slogan.getStyleClass().add("auth-slogan");
        
        overlay.getChildren().addAll(brandName, slogan);
        imageStack.getChildren().addAll(bgImage, overlay);
        leftPane.getChildren().add(imageStack);

        // Right Side - Login Form
        VBox rightPane = new VBox(25);
        rightPane.getStyleClass().add("auth-right-pane");
        rightPane.setPrefWidth(450);
        rightPane.setPadding(new Insets(60));
        rightPane.setAlignment(Pos.CENTER_LEFT);

        Label welcome = new Label("Welcome Back");
        welcome.getStyleClass().add("auth-welcome-label");
        
        Label subText = new Label("Please sign in to your account");
        subText.getStyleClass().add("auth-subtext");

        VBox form = new VBox(15);
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("luxury-text-field");
        userField.setPrefHeight(45);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("luxury-text-field");
        passField.setPrefHeight(45);

        Button loginBtn = new Button("SIGN IN");
        loginBtn.getStyleClass().add("premium-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(50);

        Hyperlink registerLink = new Hyperlink("New member? Create an account");
        registerLink.getStyleClass().add("luxury-link");
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

        form.getChildren().addAll(new Label("Username"), userField, new Label("Password"), passField, loginBtn);
        rightPane.getChildren().addAll(welcome, subText, form, registerLink, errorLabel);

        getChildren().addAll(leftPane, rightPane);
    }
}
