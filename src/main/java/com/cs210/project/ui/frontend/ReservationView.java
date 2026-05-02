package com.cs210.project.ui.frontend;

import com.cs210.project.constants.Enums.EquipmentType;
import com.cs210.project.constants.Enums.InsuranceType;
import com.cs210.project.constants.Enums.ServiceType;
import com.cs210.project.constants.VehicleStatus;
import com.cs210.project.models.Account;
import com.cs210.project.models.Bill;
import com.cs210.project.models.Location;
import com.cs210.project.models.Member;
import com.cs210.project.models.Vehicle;
import com.cs210.project.repositories.BillRepository;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.MemberRepository;
import com.cs210.project.repositories.ReservationRepository;
import com.cs210.project.repositories.VehicleRepository;
import com.cs210.project.services.ReservationService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReservationView extends VBox {
    private final ReservationService resService = new ReservationService();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final Account currentUser;
    private final Vehicle preSelectedVehicle;
    private final Consumer<Node> onViewChange;

    private ComboBox<Vehicle> vehicleCombo;
    private ComboBox<Location> pickupLocCombo;
    private ComboBox<Location> returnLocCombo;
    private DatePicker pickupDatePicker;
    private DatePicker returnDatePicker;
    private Label statusLabel;
    private Label estimateLabel;
    private Label daysLabel;
    private Label selectedVehicleLabel;
    private VBox receiptLinesBox;
    private StackPane vehicleVisual;
    private final List<CheckBox> addonChecks = new ArrayList<>();
    private final List<AddonOption> addonOptions = List.of(
            new AddonOption("Wi-Fi Hotspot", EquipmentType.WIFI, 9.00),
            new AddonOption("Additional insurance (deductible)", InsuranceType.BASIC, 15.00),
            new AddonOption("Child Seat", EquipmentType.CHILD_SEAT, 7.00),
            new AddonOption("Car fridge", EquipmentType.CAR_FRIDGE, 12.00),
            new AddonOption("Additional Driver", ServiceType.ADDITIONAL_DRIVER, 25.00)
    );

    public ReservationView(Account currentUser) {
        this(currentUser, null, null);
    }

    public ReservationView(Account currentUser, Vehicle preSelectedVehicle) {
        this(currentUser, preSelectedVehicle, null);
    }

    public ReservationView(Account currentUser, Vehicle preSelectedVehicle, Consumer<Node> onViewChange) {
        this.currentUser = currentUser;
        this.preSelectedVehicle = preSelectedVehicle;
        this.onViewChange = onViewChange;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("booking-root");
        setPadding(new Insets(22));

        VBox page = new VBox(18);
        page.getStyleClass().add("booking-page");

        Button backBtn = new Button(preSelectedVehicle != null ? "Back to car" : "Back to cars");
        backBtn.getStyleClass().add("vehicle-detail-back");
        backBtn.setOnAction(e -> goBack());

        HBox hero = new HBox(22);
        hero.getStyleClass().add("booking-hero");
        hero.setAlignment(Pos.CENTER_LEFT);

        VBox intro = new VBox(10);
        HBox.setHgrow(intro, Priority.ALWAYS);
        Label eyebrow = new Label("BOOKING DETAILS");
        eyebrow.getStyleClass().add("booking-eyebrow");
        Label title = new Label(preSelectedVehicle != null
                ? "Reserve " + preSelectedVehicle.getMake() + " " + preSelectedVehicle.getModel()
                : "Create your reservation");
        title.getStyleClass().add("booking-title");
        title.setWrapText(true);
        Label subtitle = new Label("Choose pickup dates, confirm locations, add optional services, then submit your reservation.");
        subtitle.getStyleClass().add("booking-subtitle");
        intro.getChildren().addAll(eyebrow, title, subtitle);

        vehicleVisual = new StackPane();
        vehicleVisual.getStyleClass().add("booking-vehicle-visual");
        vehicleVisual.setPrefSize(290, 170);
        hero.getChildren().addAll(intro, vehicleVisual);

        HBox body = new HBox(18);
        body.setAlignment(Pos.TOP_LEFT);

        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("booking-card");
        HBox.setHgrow(formCard, Priority.ALWAYS);

        selectedVehicleLabel = new Label();
        selectedVehicleLabel.getStyleClass().add("booking-selected-car");

        GridPane bookingGrid = createBookingGrid();
        VBox addons = createAddonsSection();
        formCard.getChildren().addAll(selectedVehicleLabel, bookingGrid, addons);

        VBox summaryCard = createSummaryCard();
        body.getChildren().addAll(formCard, summaryCard);
        page.getChildren().addAll(backBtn, hero, body);

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.getStyleClass().add("booking-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);

        configureInitialValues();
        updateVehiclePreview();
        updateEstimate();
    }

    private GridPane createBookingGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("booking-grid");
        grid.setHgap(14);
        grid.setVgap(12);

        vehicleCombo = new ComboBox<>();
        vehicleCombo.getStyleClass().add("booking-input");
        vehicleCombo.setMaxWidth(Double.MAX_VALUE);
        vehicleCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Vehicle vehicle) {
                return vehicle == null ? "" : vehicle.getMake() + " " + vehicle.getModel() + " - $" + String.format("%.0f", vehicle.getPricePerDay()) + "/day";
            }
            @Override public Vehicle fromString(String string) { return null; }
        });
        List<Vehicle> vehicles = vehicleRepo.findAll().stream()
                .filter(v -> v.isActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());
        vehicleCombo.setItems(FXCollections.observableArrayList(vehicles));

        List<Location> locations = locationRepo.findAll();
        pickupLocCombo = new ComboBox<>(FXCollections.observableArrayList(locations));
        returnLocCombo = new ComboBox<>(FXCollections.observableArrayList(locations));
        pickupLocCombo.getStyleClass().add("booking-input");
        returnLocCombo.getStyleClass().add("booking-input");
        pickupLocCombo.setMaxWidth(Double.MAX_VALUE);
        returnLocCombo.setMaxWidth(Double.MAX_VALUE);

        pickupDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        returnDatePicker = new DatePicker(LocalDate.now().plusDays(2));
        pickupDatePicker.getStyleClass().add("booking-input");
        returnDatePicker.getStyleClass().add("booking-input");
        pickupDatePicker.setEditable(false);
        returnDatePicker.setEditable(false);
        pickupDatePicker.setDayCellFactory(picker -> unavailableBefore(LocalDate.now()));
        returnDatePicker.setDayCellFactory(picker -> unavailableBefore(LocalDate.now().plusDays(1)));

        addField(grid, 0, "Vehicle", vehicleCombo);
        addField(grid, 1, "Pickup location", pickupLocCombo);
        addField(grid, 2, "Return location", returnLocCombo);
        addField(grid, 3, "Pickup date", pickupDatePicker);
        addField(grid, 4, "Return date", returnDatePicker);

        vehicleCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                pickupLocCombo.getItems().stream()
                        .filter(location -> location.getId() == newValue.getLocationId())
                        .findFirst()
                        .ifPresent(location -> {
                            pickupLocCombo.setValue(location);
                            if (returnLocCombo.getValue() == null) {
                                returnLocCombo.setValue(location);
                            }
                        });
            }
            updateVehiclePreview();
            updateEstimate();
        });
        pickupDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> updateEstimate());
        returnDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> updateEstimate());

        return grid;
    }

    private DateCell unavailableBefore(LocalDate minDate) {
        return new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(minDate));
            }
        };
    }

    private VBox createAddonsSection() {
        VBox section = new VBox(12);
        Label title = new Label("Optional add-ons");
        title.getStyleClass().add("booking-section-title");

        FlowPane addonsFlow = new FlowPane(10, 10);
        for (AddonOption option : addonOptions) {
            CheckBox checkBox = createAddonCheck(option);
            addonChecks.add(checkBox);
            addonsFlow.getChildren().add(checkBox);
        }

        section.getChildren().addAll(title, addonsFlow);
        return section;
    }

    private CheckBox createAddonCheck(AddonOption option) {
        CheckBox checkBox = new CheckBox(option.label() + "  $" + String.format("%.0f", option.price()));
        checkBox.setUserData(option);
        checkBox.getStyleClass().add("booking-addon");
        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> updateEstimate());
        return checkBox;
    }

    private VBox createSummaryCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("booking-summary-card");
        card.setPrefWidth(330);

        Label title = new Label("Reservation summary");
        title.getStyleClass().add("booking-summary-title");
        daysLabel = new Label();
        daysLabel.getStyleClass().add("booking-summary-muted");
        estimateLabel = new Label();
        estimateLabel.getStyleClass().add("booking-estimate");

        receiptLinesBox = new VBox(8);
        receiptLinesBox.getStyleClass().add("booking-receipt-lines");

        VBox included = new VBox(8,
                createSummaryLine("Daily mileage", "300 km/day"),
                createSummaryLine("Fuel policy", "Return same level"),
                createSummaryLine("Status", "Confirmed after submit"));

        Button submitBtn = new Button("Confirm reservation");
        submitBtn.getStyleClass().add("booking-submit");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> submitReservation());

        statusLabel = new Label();
        statusLabel.getStyleClass().add("booking-status");
        statusLabel.setWrapText(true);

        card.getChildren().addAll(title, daysLabel, receiptLinesBox, estimateLabel, included, submitBtn, statusLabel);
        return card;
    }

    private HBox createSummaryLine(String label, String value) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label left = new Label(label);
        left.getStyleClass().add("booking-summary-muted");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label right = new Label(value);
        right.getStyleClass().add("booking-summary-value");
        row.getChildren().addAll(left, spacer, right);
        return row;
    }

    private void addField(GridPane grid, int row, String label, Node node) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("booking-field-label");
        GridPane.setHgrow(node, Priority.ALWAYS);
        grid.add(labelNode, 0, row);
        grid.add(node, 1, row);
    }

    private void configureInitialValues() {
        if (preSelectedVehicle != null) {
            vehicleCombo.getItems().stream()
                    .filter(vehicle -> vehicle.getId() == preSelectedVehicle.getId())
                    .findFirst()
                    .ifPresent(vehicleCombo::setValue);
            vehicleCombo.setDisable(true);
        } else if (!vehicleCombo.getItems().isEmpty()) {
            vehicleCombo.setValue(vehicleCombo.getItems().get(0));
        }

        if (vehicleCombo.getValue() != null) {
            pickupLocCombo.getItems().stream()
                    .filter(location -> location.getId() == vehicleCombo.getValue().getLocationId())
                    .findFirst()
                    .ifPresent(location -> {
                        pickupLocCombo.setValue(location);
                        returnLocCombo.setValue(location);
                    });
        }
        if (returnLocCombo.getValue() == null && !returnLocCombo.getItems().isEmpty()) {
            returnLocCombo.setValue(returnLocCombo.getItems().get(0));
        }
    }

    private void updateVehiclePreview() {
        vehicleVisual.getChildren().clear();
        Vehicle vehicle = vehicleCombo.getValue();
        if (vehicle == null) {
            selectedVehicleLabel.setText("Choose an available automobile to continue.");
            return;
        }

        selectedVehicleLabel.setText(vehicle.getMake() + " " + vehicle.getModel() + " selected");
        if (vehicle.getImagePath() != null && !vehicle.getImagePath().isBlank()) {
            try {
                ImageView imageView = new ImageView(new Image(vehicle.getImagePath(), 290, 170, true, true, true));
                imageView.setFitWidth(290);
                imageView.setFitHeight(170);
                imageView.setPreserveRatio(true);
                vehicleVisual.getChildren().add(imageView);
                return;
            } catch (Exception ignored) {
                // Use placeholder below.
            }
        }
        VBox placeholder = new VBox(4);
        placeholder.setAlignment(Pos.CENTER);
        Label make = new Label(vehicle.getMake());
        make.getStyleClass().add("booking-placeholder-make");
        Label model = new Label(vehicle.getModel());
        model.getStyleClass().add("booking-placeholder-model");
        placeholder.getChildren().addAll(make, model);
        vehicleVisual.getChildren().add(placeholder);
    }

    private void updateEstimate() {
        Vehicle vehicle = vehicleCombo == null ? null : vehicleCombo.getValue();
        long days = calculateDays();
        double base = vehicle == null ? 0 : vehicle.getPricePerDay() * days;
        List<AddonOption> selectedAddons = selectedAddonOptions();
        double addons = selectedAddons.stream().mapToDouble(AddonOption::price).sum();
        daysLabel.setText(days + (days == 1 ? " rental day" : " rental days"));

        if (receiptLinesBox != null) {
            receiptLinesBox.getChildren().clear();
            String baseLabel = vehicle == null ? "Base rental" : "Base rental: " + vehicle.getMake() + " " + vehicle.getModel();
            receiptLinesBox.getChildren().add(createReceiptLine(baseLabel, String.format("%d x $%.2f", days, vehicle == null ? 0 : vehicle.getPricePerDay()), base));
            for (AddonOption option : selectedAddons) {
                receiptLinesBox.getChildren().add(createReceiptLine(option.label(), "Add-on", option.price()));
            }
            if (selectedAddons.isEmpty()) {
                receiptLinesBox.getChildren().add(createReceiptNote("No additional services selected."));
            }
        }

        estimateLabel.setText(String.format("Estimated total: $%.2f", base + addons));
    }

    private HBox createReceiptLine(String label, String detail, double amount) {
        HBox row = new HBox(8);
        row.getStyleClass().add("booking-receipt-line");
        row.setAlignment(Pos.CENTER_LEFT);
        VBox text = new VBox(2);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("booking-receipt-label");
        Label detailNode = new Label(detail);
        detailNode.getStyleClass().add("booking-receipt-detail");
        text.getChildren().addAll(labelNode, detailNode);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label amountNode = new Label(String.format("$%.2f", amount));
        amountNode.getStyleClass().add("booking-receipt-amount");
        row.getChildren().addAll(text, spacer, amountNode);
        return row;
    }

    private Label createReceiptNote(String text) {
        Label note = new Label(text);
        note.getStyleClass().add("booking-receipt-note");
        return note;
    }

    private List<AddonOption> selectedAddonOptions() {
        return addonChecks.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> (AddonOption) checkBox.getUserData())
                .collect(Collectors.toList());
    }

    private long calculateDays() {
        if (pickupDatePicker == null || returnDatePicker == null || pickupDatePicker.getValue() == null || returnDatePicker.getValue() == null) {
            return 1;
        }
        long days = ChronoUnit.DAYS.between(pickupDatePicker.getValue(), returnDatePicker.getValue());
        return Math.max(days, 1);
    }

    private void submitReservation() {
        try {
            Vehicle vehicle = vehicleCombo.getValue();
            Location pickup = pickupLocCombo.getValue();
            Location returnLocation = returnLocCombo.getValue();
            if (vehicle == null || pickup == null || returnLocation == null || pickupDatePicker.getValue() == null || returnDatePicker.getValue() == null) {
                showStatus("Please choose vehicle, locations, pickup date, and return date.", false);
                return;
            }

            LocalDateTime pickupDate = pickupDatePicker.getValue().atTime(10, 0);
            LocalDateTime returnDate = returnDatePicker.getValue().atTime(12, 0);
            if (!returnDate.isAfter(pickupDate)) {
                showStatus("Return date must be after pickup date.", false);
                return;
            }

            Member member = memberRepo.findByAccountId(currentUser.getId());
            if (member == null) {
                showStatus("Only members can create reservations.", false);
                return;
            }

            String reservationNumber = resService.createReservation(
                    member.getId(),
                    vehicle.getId(),
                    pickup.getId(),
                    returnLocation.getId(),
                    pickupDate,
                    returnDate,
                    selectedInsuranceTypes(),
                    selectedEquipmentTypes(),
                    selectedServiceTypes());

            Bill bill = null;
            com.cs210.project.models.VehicleReservation reservation = new ReservationRepository().findByNumber(reservationNumber);
            if (reservation != null) {
                bill = new BillRepository().findByReservationId(reservation.getId());
            }
            String amount = bill != null && bill.getTotalAmount() != null ? " Estimated bill: $" + bill.getTotalAmount() + "." : "";
            showStatus("Reservation " + reservationNumber + " created successfully." + amount, true);
        } catch (Exception ex) {
            showStatus("Reservation error: " + ex.getMessage(), false);
        }
    }

    private List<InsuranceType> selectedInsuranceTypes() {
        return addonChecks.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> (AddonOption) checkBox.getUserData())
                .map(AddonOption::type)
                .filter(InsuranceType.class::isInstance)
                .map(InsuranceType.class::cast)
                .collect(Collectors.toList());
    }

    private List<EquipmentType> selectedEquipmentTypes() {
        return addonChecks.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> (AddonOption) checkBox.getUserData())
                .map(AddonOption::type)
                .filter(EquipmentType.class::isInstance)
                .map(EquipmentType.class::cast)
                .collect(Collectors.toList());
    }

    private List<ServiceType> selectedServiceTypes() {
        return addonChecks.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> (AddonOption) checkBox.getUserData())
                .map(AddonOption::type)
                .filter(ServiceType.class::isInstance)
                .map(ServiceType.class::cast)
                .collect(Collectors.toList());
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("booking-status-success", "booking-status-error");
        statusLabel.getStyleClass().add(success ? "booking-status-success" : "booking-status-error");
    }

    private void goBack() {
        if (onViewChange == null) {
            return;
        }
        if (preSelectedVehicle != null) {
            onViewChange.accept(new VehicleDetailView(currentUser, preSelectedVehicle, onViewChange));
        } else {
            onViewChange.accept(new VehiclesView(currentUser, onViewChange));
        }
    }

    private record AddonOption(String label, Object type, double price) {}
}
