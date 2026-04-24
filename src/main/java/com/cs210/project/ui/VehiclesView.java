package com.cs210.project.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class VehiclesView extends VBox {

    public VehiclesView() {
        setSpacing(10);
        setPadding(new Insets(16));
        setStyle("-fx-text-fill: #1f2a37; -fx-text-background-color: #1f2a37;");

        Label title = new Label("Vehicles");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: #1f2a37;");

        Label description = new Label("Vehicle management page can be implemented here.");
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #5f6978;");

        VBox card = new VBox(8, title, description);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");

        getChildren().add(card);
    }
}
