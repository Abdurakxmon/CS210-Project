package com.cs210.project.ui;

import com.cs210.project.models.Account;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class DashboardView extends VBox {

    public DashboardView(Account currentAccount) {
        setSpacing(16);
        setPadding(new Insets(16));
        setStyle("-fx-text-fill: #1f2a37; -fx-text-background-color: #1f2a37;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: #1f2a37;");

        Label subtitle = new Label("Overview of your account");
        subtitle.setStyle("-fx-text-fill: #5f6978;");

        VBox header = new VBox(4, title, subtitle);

        Label welcomeLabel = new Label("Welcome, " + displayName(currentAccount));
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #1f2a37;");

        VBox infoCard = new VBox(
                8,
                welcomeLabel,
                infoRow("Role", currentAccount == null ? "-" : currentAccount.getRoleLabel()),
                infoRow("Email", currentAccount == null ? "-" : fallback(currentAccount.getEmail())),
                infoRow("Status", currentAccount == null ? "-" : currentAccount.getStatusLabel())
        );
        infoCard.setPadding(new Insets(16));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");

        Label noteLabel = new Label("Use the left menu to open Vehicles and Accounts.");
        noteLabel.setWrapText(true);
        noteLabel.setStyle("-fx-text-fill: #5f6978;");

        VBox noteCard = new VBox(8, noteLabel);
        noteCard.setPadding(new Insets(16));
        noteCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");

        getChildren().addAll(header, infoCard, noteCard);
    }

    private HBox infoRow(String key, String value) {
        Label keyLabel = new Label(key + ":");
        keyLabel.setMinWidth(72);
        keyLabel.setStyle("-fx-font-weight: 700; -fx-text-fill: #5f6978;");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #1f2a37;");
        return new HBox(8, keyLabel, valueLabel);
    }

    private String fallback(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String displayName(Account account) {
        if (account == null) {
            return "Guest";
        }
        return account.getDisplayName();
    }
}
