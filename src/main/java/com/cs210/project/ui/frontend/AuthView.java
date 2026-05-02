package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.services.AuthService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class AuthView extends StackPane {
    private final AuthService authService = new AuthService();
    private final Consumer<Account> onLoginSuccess;
    private final Runnable onRegisterClick;

    public AuthView(Consumer<Account> onLoginSuccess, Runnable onRegisterClick) {
        this.onLoginSuccess = onLoginSuccess;
        this.onRegisterClick = onRegisterClick;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("auth-root");

        HBox shell = new HBox();
        shell.getStyleClass().add("auth-shell");

        VBox leftPane = new VBox(14);
        leftPane.getStyleClass().add("auth-hero-pane");
        leftPane.setPadding(new Insets(46));
        leftPane.setAlignment(Pos.BOTTOM_LEFT);

        Label badge = new Label("CS210 FLEET SUITE");
        badge.getStyleClass().add("auth-hero-badge");

        ImageView heroImage = new ImageView(new Image(
                "https://images.unsplash.com/photo-1493238792000-8113da705763?auto=format&fit=crop&w=1000&q=80",
                true
        ));
        heroImage.setPreserveRatio(true);
        heroImage.setFitWidth(420);
        heroImage.getStyleClass().add("auth-hero-image");
        StackPane heroImageCard = new StackPane(heroImage);
        heroImageCard.getStyleClass().add("auth-hero-image-card");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label brandName = new Label("Drive Better.\nMove Smarter.");
        brandName.getStyleClass().add("auth-hero-title");
        Label slogan = new Label("Fast bookings, secure accounts, and a premium rental flow in one place.");
        slogan.getStyleClass().add("auth-hero-subtitle");

        leftPane.getChildren().addAll(badge, heroImageCard, spacer, brandName, slogan);

        VBox rightPane = new VBox();
        rightPane.getStyleClass().add("auth-panel-wrap");
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(28));

        VBox card = new VBox(14);
        card.getStyleClass().add("auth-card");
        card.setMaxWidth(440);

        Label welcome = new Label("Welcome Back");
        welcome.getStyleClass().add("auth-title");
        Label subText = new Label("Sign in to access your dashboard.");
        subText.getStyleClass().add("auth-subtitle");

        VBox form = new VBox(10);
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("auth-input");
        userField.setPrefHeight(45);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("auth-input");
        passField.setPrefHeight(45);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("auth-feedback");
        errorLabel.setWrapText(true);

        ProgressIndicator loading = new ProgressIndicator();
        loading.getStyleClass().add("auth-loading");
        loading.setMaxSize(18, 18);
        loading.setVisible(false);
        loading.setManaged(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("auth-primary-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(50);

        Hyperlink registerLink = new Hyperlink("New member? Create account");
        registerLink.getStyleClass().add("auth-link");
        registerLink.setOnAction(e -> onRegisterClick.run());

        loginBtn.setOnAction(e -> {
            String username = userField.getText() == null ? "" : userField.getText().trim();
            String password = passField.getText() == null ? "" : passField.getText();
            if (username.isBlank() || password.isBlank()) {
                errorLabel.setText("Enter both username and password.");
                return;
            }

            setBusy(true, loading, loginBtn, userField, passField);
            errorLabel.setText("");

            Task<Boolean> loginTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return authService.login(username, password);
                }
            };

            loginTask.setOnSucceeded(ev -> {
                setBusy(false, loading, loginBtn, userField, passField);
                if (Boolean.TRUE.equals(loginTask.getValue())) {
                    onLoginSuccess.accept(Session.getAccount());
                } else {
                    errorLabel.setText("Invalid username or password.");
                }
            });
            loginTask.setOnFailed(ev -> {
                setBusy(false, loading, loginBtn, userField, passField);
                String message = loginTask.getException() != null && loginTask.getException().getMessage() != null
                        ? loginTask.getException().getMessage()
                        : "Could not sign in right now. Please try again.";
                errorLabel.setText(message);
            });

            Thread worker = new Thread(loginTask, "auth-login-task");
            worker.setDaemon(true);
            worker.start();
        });

        passField.setOnAction(e -> loginBtn.fire());
        userField.setOnAction(e -> passField.requestFocus());

        HBox actionRow = new HBox(10, loading, registerLink);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        form.getChildren().addAll(userField, passField, loginBtn);
        card.getChildren().addAll(welcome, subText, form, errorLabel, actionRow);
        rightPane.getChildren().add(card);

        leftPane.setMinWidth(320);
        rightPane.setMinWidth(360);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        shell.getChildren().addAll(leftPane, rightPane);
        getChildren().add(shell);

        widthProperty().addListener((obs, oldVal, newVal) -> applyResponsiveLayout(newVal.doubleValue(), leftPane, card));
        applyResponsiveLayout(900, leftPane, card);
    }

    private void applyResponsiveLayout(double width, Region heroPane, VBox card) {
        boolean compact = width < 930;
        heroPane.setVisible(!compact);
        heroPane.setManaged(!compact);
        card.setMaxWidth(compact ? 560 : 440);
    }

    private void setBusy(boolean busy, ProgressIndicator loading, Button loginBtn, TextField userField, PasswordField passField) {
        loading.setVisible(busy);
        loading.setManaged(busy);
        loginBtn.setDisable(busy);
        userField.setDisable(busy);
        passField.setDisable(busy);
    }
}
