package com.cs210.project.ui.frontend;

import com.cs210.project.services.AuthService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class RegistrationView extends BorderPane {
    private final AuthService authService = new AuthService();
    private final Runnable onBack;
    private static final int MINIMUM_AGE = 18;
    private static final String PASSWORD_RULE = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    private enum MessageTone { ERROR, SUCCESS, INFO }

    public RegistrationView(Runnable onBack) {
        this.onBack = onBack;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("register-root");

        HBox shell = new HBox();
        shell.getStyleClass().add("register-shell");

        VBox infoPane = new VBox(12);
        infoPane.getStyleClass().add("register-info-pane");
        infoPane.setPadding(new Insets(44));
        infoPane.setAlignment(Pos.TOP_LEFT);

        Label infoBadge = new Label("SMART MEMBER ONBOARDING");
        infoBadge.getStyleClass().add("register-info-badge");
        Label infoTitle = new Label("Create your account in one quick flow.");
        infoTitle.getStyleClass().add("register-info-title");
        Label infoBody = new Label(
                "Strong passwords, age verification, and license validity checks are built in for account safety.");
        infoBody.getStyleClass().add("register-info-body");
        infoBody.setWrapText(true);

        ImageView infoImage = new ImageView(new Image(
                "https://images.unsplash.com/photo-1549924231-f129b911e442?auto=format&fit=crop&w=1000&q=80",
                true
        ));
        infoImage.setPreserveRatio(true);
        infoImage.setFitWidth(420);
        infoImage.getStyleClass().add("register-info-image");
        StackPane infoImageCard = new StackPane(infoImage);
        infoImageCard.getStyleClass().add("register-info-image-card");

        Label checklistTitle = new Label("Live password checklist");
        checklistTitle.getStyleClass().add("register-check-title");
        VBox checklist = new VBox(8);

        Label minLenIcon = new Label("*");
        minLenIcon.getStyleClass().add("register-rule-icon");
        Label minLenText = new Label("Minimum 8 characters");
        minLenText.getStyleClass().add("register-rule-text");
        HBox minLenRow = new HBox(8, minLenIcon, minLenText);
        minLenRow.getStyleClass().addAll("register-rule-row", "register-rule-neutral");

        Label upperIcon = new Label("*");
        upperIcon.getStyleClass().add("register-rule-icon");
        Label upperText = new Label("At least 1 uppercase letter");
        upperText.getStyleClass().add("register-rule-text");
        HBox upperRow = new HBox(8, upperIcon, upperText);
        upperRow.getStyleClass().addAll("register-rule-row", "register-rule-neutral");

        Label numberIcon = new Label("*");
        numberIcon.getStyleClass().add("register-rule-icon");
        Label numberText = new Label("At least 1 number");
        numberText.getStyleClass().add("register-rule-text");
        HBox numberRow = new HBox(8, numberIcon, numberText);
        numberRow.getStyleClass().addAll("register-rule-row", "register-rule-neutral");

        Label matchIcon = new Label("*");
        matchIcon.getStyleClass().add("register-rule-icon");
        Label matchText = new Label("Re-entered password matches");
        matchText.getStyleClass().add("register-rule-text");
        HBox matchRow = new HBox(8, matchIcon, matchText);
        matchRow.getStyleClass().addAll("register-rule-row", "register-rule-neutral");

        checklist.getChildren().addAll(minLenRow, upperRow, numberRow, matchRow);

        Region infoSpacer = new Region();
        VBox.setVgrow(infoSpacer, Priority.ALWAYS);
        Label infoHint = new Label("Other requirements: age must be 18+ and license expiry must be in the future.");
        infoHint.getStyleClass().add("register-info-hint");
        infoHint.setWrapText(true);

        infoPane.getChildren().addAll(
                infoBadge, infoTitle, infoBody, infoImageCard, checklistTitle, checklist, infoSpacer, infoHint
        );

        VBox formPane = new VBox();
        formPane.getStyleClass().add("register-form-wrap");
        formPane.setPadding(new Insets(26));
        formPane.setAlignment(Pos.CENTER);

        VBox card = new VBox(16);
        card.getStyleClass().add("register-card-modern");
        card.setMaxWidth(620);
        card.setFillWidth(true);

        Label title = new Label("Create Member Account");
        title.getStyleClass().add("register-title");
        Label subtitle = new Label("Fill in your details to start booking.");
        subtitle.getStyleClass().add("register-subtitle");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("register-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.getStyleClass().add("register-form-grid");

        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(50);
        leftCol.setHgrow(Priority.ALWAYS);
        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(50);
        rightCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(leftCol, rightCol);

        TextField nameField = createStyledTextField("Full Name");
        TextField phoneField = createStyledTextField("Phone");
        TextField emailField = createStyledTextField("Email");
        TextField addressField = createStyledTextField("Address");
        TextField cityField = createStyledTextField("City");
        TextField stateField = createStyledTextField("State");
        TextField zipcodeField = createStyledTextField("Zipcode");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Birth Date");
        configureDatePicker(birthDatePicker);

        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.getStyleClass().add("auth-input");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter Password");
        confirmPasswordField.setPrefHeight(40);
        confirmPasswordField.getStyleClass().add("auth-input");

        TextField licenseField = createStyledTextField("Driver License #");
        DatePicker expiryPicker = new DatePicker();
        expiryPicker.setPromptText("License Expiry");
        configureDatePicker(expiryPicker);

        int r = 0;
        grid.add(createSectionLabel("Personal Information"), 0, r++, 2, 1);
        grid.add(nameField, 0, r);
        grid.add(phoneField, 1, r++);
        grid.add(emailField, 0, r);
        grid.add(birthDatePicker, 1, r++);

        grid.add(createSectionLabel("Address"), 0, r++, 2, 1);
        grid.add(addressField, 0, r, 2, 1);
        r++;
        grid.add(cityField, 0, r);
        grid.add(stateField, 1, r++);
        grid.add(zipcodeField, 0, r++, 2, 1);

        grid.add(createSectionLabel("Account & License"), 0, r++, 2, 1);
        grid.add(usernameField, 0, r);
        grid.add(passwordField, 1, r++);
        grid.add(new Label(""), 0, r);
        grid.add(confirmPasswordField, 1, r++);
        grid.add(licenseField, 0, r);
        grid.add(expiryPicker, 1, r++);

        scrollPane.setContent(grid);

        Button registerBtn = new Button("Create Account");
        registerBtn.getStyleClass().add("auth-primary-btn");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(48);

        Button backBtn = new Button("Back to Login");
        backBtn.getStyleClass().add("auth-secondary-btn");
        backBtn.setOnAction(e -> onBack.run());

        ProgressIndicator loading = new ProgressIndicator();
        loading.getStyleClass().add("auth-loading");
        loading.setMaxSize(18, 18);
        loading.setVisible(false);
        loading.setManaged(false);

        HBox messageBox = new HBox(10);
        messageBox.getStyleClass().add("register-message");
        messageBox.setVisible(false);
        messageBox.setManaged(false);

        Label msgIcon = new Label("!");
        msgIcon.getStyleClass().add("register-message-icon");
        Label msgLabel = new Label();
        msgLabel.getStyleClass().add("register-message-text");
        msgLabel.setWrapText(true);
        messageBox.getChildren().addAll(msgIcon, msgLabel);

        Runnable refreshPasswordUi = () -> {
            String password = passwordField.getText() == null ? "" : passwordField.getText();
            String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

            boolean hasPasswordInput = !password.isBlank();
            boolean hasConfirmInput = !confirmPassword.isBlank();

            boolean minLenOk = password.length() >= 8;
            boolean upperOk = password.chars().anyMatch(Character::isUpperCase);
            boolean numberOk = password.chars().anyMatch(Character::isDigit);
            boolean matchOk = hasConfirmInput && password.equals(confirmPassword);
            boolean allPasswordRulesOk = minLenOk && upperOk && numberOk;

            updateRuleRow(minLenRow, minLenIcon, hasPasswordInput, minLenOk);
            updateRuleRow(upperRow, upperIcon, hasPasswordInput, upperOk);
            updateRuleRow(numberRow, numberIcon, hasPasswordInput, numberOk);
            updateRuleRow(matchRow, matchIcon, hasConfirmInput, matchOk);

            applyInputValidationStyle(passwordField, hasPasswordInput ? allPasswordRulesOk : null);
            applyInputValidationStyle(confirmPasswordField, hasConfirmInput ? matchOk : null);
        };

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> refreshPasswordUi.run());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> refreshPasswordUi.run());
        refreshPasswordUi.run();

        registerBtn.setOnAction(e -> {
            if (nameField.getText().isBlank() || emailField.getText().isBlank() ||
                    usernameField.getText().isBlank() || passwordField.getText().isBlank() ||
                    confirmPasswordField.getText().isBlank() ||
                    licenseField.getText().isBlank() || expiryPicker.getValue() == null ||
                    birthDatePicker.getValue() == null) {
                setMessage(messageBox, msgIcon, msgLabel, "Please fill all required fields.", MessageTone.ERROR);
                return;
            }

            String validationError = validateRegistrationInputs(
                    passwordField.getText(),
                    confirmPasswordField.getText(),
                    birthDatePicker.getValue(),
                    expiryPicker.getValue()
            );
            if (validationError != null) {
                setMessage(messageBox, msgIcon, msgLabel, validationError, MessageTone.ERROR);
                return;
            }

            setMessage(messageBox, msgIcon, msgLabel, "Creating your account...", MessageTone.INFO);
            setBusy(true, loading, registerBtn, backBtn,
                    nameField, phoneField, emailField, addressField, cityField, stateField, zipcodeField,
                    birthDatePicker, usernameField, passwordField, confirmPasswordField, licenseField, expiryPicker);

            Task<Boolean> registerTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return authService.register(
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
                }
            };

            registerTask.setOnSucceeded(ev -> {
                setBusy(false, loading, registerBtn, backBtn,
                        nameField, phoneField, emailField, addressField, cityField, stateField, zipcodeField,
                        birthDatePicker, usernameField, passwordField, confirmPasswordField, licenseField, expiryPicker);

                if (Boolean.TRUE.equals(registerTask.getValue())) {
                    setMessage(messageBox, msgIcon, msgLabel,
                            "Registration successful. You can now sign in.", MessageTone.SUCCESS);
                } else {
                    setMessage(messageBox, msgIcon, msgLabel,
                            "Registration failed. Username or license may already exist.", MessageTone.ERROR);
                }
            });

            registerTask.setOnFailed(ev -> {
                setBusy(false, loading, registerBtn, backBtn,
                        nameField, phoneField, emailField, addressField, cityField, stateField, zipcodeField,
                        birthDatePicker, usernameField, passwordField, confirmPasswordField, licenseField, expiryPicker);
                String error = registerTask.getException() != null && registerTask.getException().getMessage() != null
                        ? registerTask.getException().getMessage()
                        : "Registration failed due to a system error.";
                setMessage(messageBox, msgIcon, msgLabel, error, MessageTone.ERROR);
            });

            Thread worker = new Thread(registerTask, "auth-register-task");
            worker.setDaemon(true);
            worker.start();
        });

        HBox actionRow = new HBox(10, loading, registerBtn, backBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(registerBtn, Priority.ALWAYS);

        card.getChildren().addAll(title, subtitle, messageBox, scrollPane, actionRow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        formPane.getChildren().add(card);

        infoPane.setMinWidth(320);
        formPane.setMinWidth(380);
        HBox.setHgrow(infoPane, Priority.ALWAYS);
        HBox.setHgrow(formPane, Priority.ALWAYS);
        shell.getChildren().addAll(infoPane, formPane);

        setCenter(shell);
        widthProperty().addListener((obs, oldVal, newVal) -> applyResponsiveLayout(newVal.doubleValue(), infoPane, card));
        applyResponsiveLayout(900, infoPane, card);
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("register-section-label");
        return label;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        field.getStyleClass().add("auth-input");
        return field;
    }

    private void configureDatePicker(DatePicker picker) {
        picker.getStyleClass().add("auth-input");
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setEditable(false);
    }

    private String validateRegistrationInputs(String password, String confirmPassword,
                                              LocalDate birthDate, LocalDate licenseExpiry) {
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        if (!password.matches(PASSWORD_RULE)) {
            return "Password must be at least 8 characters, include 1 uppercase letter and 1 number.";
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(MINIMUM_AGE))) {
            return "You must be at least " + MINIMUM_AGE + " years old.";
        }
        if (!licenseExpiry.isAfter(LocalDate.now())) {
            return "License expiry must be a future date.";
        }
        return null;
    }

    private void applyResponsiveLayout(double width, Region infoPane, VBox card) {
        boolean compact = width < 960;
        infoPane.setVisible(!compact);
        infoPane.setManaged(!compact);
        card.setMaxWidth(compact ? 700 : 620);
    }

    private void setBusy(boolean busy, ProgressIndicator loading, Button registerBtn,
                         Button backBtn, Control... controls) {
        loading.setVisible(busy);
        loading.setManaged(busy);
        registerBtn.setDisable(busy);
        backBtn.setDisable(busy);
        for (Control control : controls) {
            control.setDisable(busy);
        }
    }

    private void updateRuleRow(HBox row, Label icon, boolean active, boolean valid) {
        row.getStyleClass().removeAll("register-rule-neutral", "register-rule-valid", "register-rule-invalid");
        if (!active) {
            row.getStyleClass().add("register-rule-neutral");
            icon.setText("*");
            return;
        }
        if (valid) {
            row.getStyleClass().add("register-rule-valid");
            icon.setText("OK");
        } else {
            row.getStyleClass().add("register-rule-invalid");
            icon.setText("X");
        }
    }

    private void applyInputValidationStyle(Control input, Boolean valid) {
        input.getStyleClass().removeAll("auth-input-valid", "auth-input-invalid");
        if (valid == null) return;
        input.getStyleClass().add(valid ? "auth-input-valid" : "auth-input-invalid");
    }

    private void setMessage(HBox messageBox, Label msgIcon, Label msgLabel, String message, MessageTone tone) {
        msgLabel.setText(message);
        messageBox.getStyleClass().removeAll("register-message-error", "register-message-success", "register-message-info");

        if (tone == MessageTone.SUCCESS) {
            messageBox.getStyleClass().add("register-message-success");
            msgIcon.setText("OK");
        } else if (tone == MessageTone.INFO) {
            messageBox.getStyleClass().add("register-message-info");
            msgIcon.setText("...");
        } else {
            messageBox.getStyleClass().add("register-message-error");
            msgIcon.setText("!");
        }

        messageBox.setVisible(true);
        messageBox.setManaged(true);
    }
}

