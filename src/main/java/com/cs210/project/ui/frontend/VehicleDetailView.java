package com.cs210.project.ui.frontend;

import com.cs210.project.models.Account;
import com.cs210.project.models.Vehicle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class VehicleDetailView extends VBox {
    private final Account currentUser;
    private final Vehicle vehicle;
    private final Consumer<Node> onViewChange;

    public VehicleDetailView(Account currentUser, Vehicle vehicle, Consumer<Node> onViewChange) {
        this.currentUser = currentUser;
        this.vehicle = vehicle;
        this.onViewChange = onViewChange;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("vehicle-detail-root");
        setPadding(new Insets(22));

        VBox content = new VBox(18);
        content.getStyleClass().add("vehicle-detail-content");

        Button backBtn = new Button("Back to cars");
        backBtn.getStyleClass().add("vehicle-detail-back");
        backBtn.setOnAction(e -> {
            if (onViewChange != null) {
                onViewChange.accept(new VehiclesView(currentUser, onViewChange));
            }
        });

        HBox hero = new HBox(24);
        hero.getStyleClass().add("vehicle-detail-hero");
        hero.setAlignment(Pos.CENTER_LEFT);

        StackPane visual = createVisual();
        HBox.setHgrow(visual, Priority.ALWAYS);

        VBox summary = new VBox(14);
        summary.getStyleClass().add("vehicle-detail-summary");
        summary.setMinWidth(300);
        summary.setPrefWidth(360);

        Label eyebrow = new Label("AVAILABLE NOW");
        eyebrow.getStyleClass().add("vehicle-detail-eyebrow");
        Label title = new Label(vehicle.getMake() + " " + vehicle.getModel());
        title.getStyleClass().add("vehicle-detail-title");
        title.setWrapText(true);
        Label subtitle = new Label("Or a similar automobile in the same class");
        subtitle.getStyleClass().add("vehicle-detail-subtitle");

        Label price = new Label(String.format("$%.0f / day", vehicle.getPricePerDay()));
        price.getStyleClass().add("vehicle-detail-price");

        HBox facts = new HBox(10);
        facts.getChildren().addAll(
                createFact("Seats", String.valueOf(vehicle.getPassengerCapacity())),
                createFact("Year", String.valueOf(vehicle.getManufacturingYear())),
                createFact("Limit", "300 km/day"));

        Button reserveBtn = new Button("Reserve now");
        reserveBtn.getStyleClass().add("vehicle-detail-reserve");
        reserveBtn.setMaxWidth(Double.MAX_VALUE);
        reserveBtn.setOnAction(e -> {
            if (onViewChange != null) {
                onViewChange.accept(new ReservationView(currentUser, vehicle, onViewChange));
            }
        });

        summary.getChildren().addAll(eyebrow, title, subtitle, price, facts, reserveBtn);
        hero.getChildren().addAll(visual, summary);

        HBox body = new HBox(18);
        body.setAlignment(Pos.TOP_LEFT);

        VBox specsCard = createSpecsCard();
        VBox termsCard = createTermsCard();
        HBox.setHgrow(specsCard, Priority.ALWAYS);
        HBox.setHgrow(termsCard, Priority.ALWAYS);
        body.getChildren().addAll(specsCard, termsCard);

        content.getChildren().addAll(backBtn, hero, body);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("vehicle-detail-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    private StackPane createVisual() {
        StackPane visual = new StackPane();
        visual.getStyleClass().add("vehicle-detail-visual");
        visual.setMinHeight(330);

        if (vehicle.getImagePath() != null && !vehicle.getImagePath().isBlank()) {
            try {
                ImageView imageView = new ImageView(new Image(vehicle.getImagePath(), 620, 330, true, true, true));
                imageView.setFitWidth(620);
                imageView.setFitHeight(330);
                imageView.setPreserveRatio(true);
                visual.getChildren().add(imageView);
                return visual;
            } catch (Exception ignored) {
                // Use the branded placeholder below when a stored image path cannot load.
            }
        }

        VBox placeholder = new VBox(8);
        placeholder.setAlignment(Pos.CENTER);
        Label make = new Label(vehicle.getMake() != null ? vehicle.getMake() : "Rental");
        make.getStyleClass().add("vehicle-detail-placeholder-make");
        Label model = new Label(vehicle.getModel() != null ? vehicle.getModel() : "Automobile");
        model.getStyleClass().add("vehicle-detail-placeholder-model");
        placeholder.getChildren().addAll(make, model);
        visual.getChildren().add(placeholder);
        return visual;
    }

    private VBox createFact(String title, String value) {
        VBox fact = new VBox(4);
        fact.getStyleClass().add("vehicle-detail-fact");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("vehicle-detail-fact-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("vehicle-detail-fact-value");
        fact.getChildren().addAll(titleLabel, valueLabel);
        return fact;
    }

    private VBox createSpecsCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("vehicle-detail-card");
        Label title = new Label("Vehicle specifications");
        title.getStyleClass().add("vehicle-detail-card-title");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        addSpec(grid, 0, "Body type", vehicle.getVehicleType() != null ? vehicle.getVehicleType().getLabel() : "Vehicle");
        addSpec(grid, 1, "Class", vehicle.getCarType() != null ? vehicle.getCarType().getLabel() : "Standard");
        addSpec(grid, 2, "Transmission", vehicle.getTransmissionType() != null ? vehicle.getTransmissionType().getLabel() : "Automatic");
        addSpec(grid, 3, "Fuel", vehicle.getFuelType() != null ? vehicle.getFuelType().getLabel() : "Petrol");
        addSpec(grid, 4, "Mileage", String.format("%,d km", vehicle.getMileage()));
        addSpec(grid, 5, "Location", vehicle.getLocationName() != null ? vehicle.getLocationName() : "Main branch");

        card.getChildren().addAll(title, grid);
        return card;
    }

    private VBox createTermsCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("vehicle-detail-card");
        Label title = new Label("Rental conditions");
        title.getStyleClass().add("vehicle-detail-card-title");
        card.getChildren().addAll(
                title,
                createTerm("Daily mileage limit", "300 km are included per rental day."),
                createTerm("Deposit", "Deposit is confirmed during pickup by staff."),
                createTerm("Fuel policy", "Vehicle should be returned with the same fuel level."),
                createTerm("Support", "Pickup and return details are confirmed after reservation."));
        return card;
    }

    private HBox createTerm(String title, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);
        Label marker = new Label("");
        marker.getStyleClass().add("vehicle-detail-term-marker");
        VBox text = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("vehicle-detail-term-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("vehicle-detail-term-value");
        valueLabel.setWrapText(true);
        text.getChildren().addAll(titleLabel, valueLabel);
        row.getChildren().addAll(marker, text);
        return row;
    }

    private void addSpec(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("vehicle-detail-spec-label");
        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("vehicle-detail-spec-value");
        Region spacer = new Region();
        GridPane.setHgrow(spacer, Priority.ALWAYS);
        grid.add(labelNode, 0, row);
        grid.add(spacer, 1, row);
        grid.add(valueNode, 2, row);
    }
}
