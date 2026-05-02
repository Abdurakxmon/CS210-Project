package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.constants.Enums.VehicleType;
import com.cs210.project.constants.Enums.TransmissionType;
import com.cs210.project.constants.Enums.FuelType;
import com.cs210.project.constants.VehicleStatus;
import com.cs210.project.models.Account;
import com.cs210.project.models.Location;
import com.cs210.project.models.RentalSystem;
import com.cs210.project.models.Vehicle;
import com.cs210.project.repositories.LocationRepository;
import com.cs210.project.repositories.RentalSystemRepository;
import com.cs210.project.repositories.ParkingStallRepository;
import com.cs210.project.models.ParkingStall;
import com.cs210.project.repositories.VehicleRepository;
import com.cs210.project.constants.Enums.CarType;
import com.cs210.project.repositories.VehicleLogRepository;
import com.cs210.project.constants.Enums.VehicleLogType;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VehiclesView extends VBox {
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final RentalSystemRepository systemRepo = new RentalSystemRepository();
    private final VehicleLogRepository logRepo = new VehicleLogRepository();
    private final ParkingStallRepository stallRepo = new ParkingStallRepository();
    private final TableView<Vehicle> table = new TableView<>();
    private final TilePane vehicleCards = new TilePane();
    private final Label resultCountLabel = new Label();
    private final Label backendInventorySummaryLabel = new Label();
    private TextField memberSearchField;
    private ComboBox<VehicleType> memberTypeFilter;
    private ComboBox<CarType> memberClassFilter;
    private ComboBox<TransmissionType> memberTransmissionFilter;
    private ComboBox<FuelType> memberFuelFilter;
    private ComboBox<Location> memberLocationFilter;
    private List<Vehicle> memberCatalogVehicles = new ArrayList<>();
    private final Account currentUser;
    private java.util.function.Consumer<javafx.scene.Node> onViewChange;

    public VehiclesView(Account user) {
        this(user, null);
    }

    public VehiclesView(Account user, java.util.function.Consumer<javafx.scene.Node> onViewChange) {
        this.currentUser = user;
        this.onViewChange = onViewChange;
        setupUI();
        loadData();
    }

    private void setupUI() {
        if (Session.isMember()) {
            setupMemberCatalogUI();
        } else {
            setupManagementUI();
        }
    }

    private void setupMemberCatalogUI() {
        getStyleClass().add("vehicle-catalog-root");
        setPadding(new Insets(22));
        setSpacing(18);

        Label eyebrow = new Label("AVAILABLE AUTOMOBILES");
        eyebrow.getStyleClass().add("vehicle-catalog-eyebrow");
        Label title = new Label("Find your next rental");
        title.getStyleClass().add("vehicle-catalog-title");
        Label subtitle = new Label("Search by vehicle name, then refine by class, location, transmission, or fuel.");
        subtitle.getStyleClass().add("vehicle-catalog-subtitle");

        VBox searchWrap = new VBox(10);
        searchWrap.setAlignment(Pos.CENTER);
        searchWrap.getStyleClass().add("vehicle-search-wrap");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER);
        memberSearchField = new TextField();
        memberSearchField.setPromptText("Search Chevrolet, Malibu, Spark...");
        memberSearchField.getStyleClass().add("vehicle-search-field");
        memberSearchField.setMaxWidth(520);
        HBox.setHgrow(memberSearchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("vehicle-primary-btn");
        searchBtn.setOnAction(e -> applyMemberFilters());
        searchRow.getChildren().addAll(memberSearchField, searchBtn);

        FlowPane filters = new FlowPane(10, 10);
        filters.setAlignment(Pos.CENTER);

        memberTypeFilter = new ComboBox<>(FXCollections.observableArrayList(VehicleType.values()));
        memberTypeFilter.setPromptText("Body type");
        memberClassFilter = new ComboBox<>(FXCollections.observableArrayList(CarType.values()));
        memberClassFilter.setPromptText("Class");
        memberTransmissionFilter = new ComboBox<>(FXCollections.observableArrayList(TransmissionType.values()));
        memberTransmissionFilter.setPromptText("Transmission");
        memberFuelFilter = new ComboBox<>(FXCollections.observableArrayList(FuelType.values()));
        memberFuelFilter.setPromptText("Fuel");
        memberLocationFilter = new ComboBox<>(FXCollections.observableArrayList(locationRepo.findAll()));
        memberLocationFilter.setPromptText("Location");

        configureMemberFilterLabels();

        for (ComboBox<?> filter : List.of(memberTypeFilter, memberClassFilter, memberTransmissionFilter, memberFuelFilter, memberLocationFilter)) {
            filter.getStyleClass().add("vehicle-filter");
            filter.setPrefWidth(142);
        }

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("vehicle-secondary-btn");
        clearBtn.setOnAction(e -> {
            memberSearchField.clear();
            memberTypeFilter.setValue(null);
            memberClassFilter.setValue(null);
            memberTransmissionFilter.setValue(null);
            memberFuelFilter.setValue(null);
            memberLocationFilter.setValue(null);
            applyMemberFilters();
        });

        filters.getChildren().addAll(memberTypeFilter, memberClassFilter, memberTransmissionFilter, memberFuelFilter, memberLocationFilter, clearBtn);
        searchWrap.getChildren().addAll(eyebrow, title, subtitle, searchRow, filters);

        HBox resultHeader = new HBox(resultCountLabel);
        resultHeader.setAlignment(Pos.CENTER_LEFT);
        resultCountLabel.getStyleClass().add("vehicle-result-count");

        vehicleCards.getStyleClass().add("vehicle-card-grid");
        vehicleCards.setHgap(18);
        vehicleCards.setVgap(18);
        vehicleCards.setPrefColumns(3);
        vehicleCards.setTileAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(vehicleCards);
        scrollPane.getStyleClass().add("vehicle-catalog-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        memberSearchField.setOnAction(e -> applyMemberFilters());
        memberSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyMemberFilters());
        for (ComboBox<?> filter : List.of(memberTypeFilter, memberClassFilter, memberTransmissionFilter, memberFuelFilter, memberLocationFilter)) {
            filter.setOnAction(e -> applyMemberFilters());
        }

        getChildren().addAll(searchWrap, resultHeader, scrollPane);
    }

    private void configureMemberFilterLabels() {
        memberTypeFilter.setConverter(new StringConverter<>() {
            @Override public String toString(VehicleType value) { return value == null ? "" : value.getLabel(); }
            @Override public VehicleType fromString(String value) { return null; }
        });
        memberClassFilter.setConverter(new StringConverter<>() {
            @Override public String toString(CarType value) { return value == null ? "" : value.getLabel(); }
            @Override public CarType fromString(String value) { return null; }
        });
        memberTransmissionFilter.setConverter(new StringConverter<>() {
            @Override public String toString(TransmissionType value) { return value == null ? "" : value.getLabel(); }
            @Override public TransmissionType fromString(String value) { return null; }
        });
        memberFuelFilter.setConverter(new StringConverter<>() {
            @Override public String toString(FuelType value) { return value == null ? "" : value.getLabel(); }
            @Override public FuelType fromString(String value) { return null; }
        });
    }

    private void applyMemberFilters() {
        List<Vehicle> vehicles = memberCatalogVehicles.stream()
                .filter(this::matchesMemberFilters)
                .sorted((first, second) -> Double.compare(first.getPricePerDay(), second.getPricePerDay()))
                .collect(Collectors.toList());

        vehicleCards.getChildren().setAll(
                vehicles.stream()
                        .map(this::createVehicleCard)
                        .collect(Collectors.toList()));

        if (vehicles.isEmpty()) {
            vehicleCards.getChildren().add(createEmptyCatalogState());
        }

        resultCountLabel.setText(vehicles.size() + (vehicles.size() == 1 ? " automobile available" : " automobiles available"));
    }

    private boolean isMemberVisibleVehicle(Vehicle vehicle) {
        return vehicle != null
                && vehicle.isActive()
                && vehicle.getStatus() == VehicleStatus.AVAILABLE;
    }

    private boolean matchesMemberFilters(Vehicle vehicle) {
        String query = memberSearchField.getText() == null
                ? ""
                : memberSearchField.getText().trim().toLowerCase(Locale.ROOT);
        String name = ((vehicle.getMake() == null ? "" : vehicle.getMake()) + " "
                + (vehicle.getModel() == null ? "" : vehicle.getModel())).toLowerCase(Locale.ROOT);

        if (!query.isBlank() && !name.contains(query)) {
            return false;
        }
        if (memberTypeFilter.getValue() != null && vehicle.getVehicleType() != memberTypeFilter.getValue()) {
            return false;
        }
        if (memberClassFilter.getValue() != null && vehicle.getCarType() != memberClassFilter.getValue()) {
            return false;
        }
        if (memberTransmissionFilter.getValue() != null && vehicle.getTransmissionType() != memberTransmissionFilter.getValue()) {
            return false;
        }
        if (memberFuelFilter.getValue() != null && vehicle.getFuelType() != memberFuelFilter.getValue()) {
            return false;
        }
        return memberLocationFilter.getValue() == null || vehicle.getLocationId() == memberLocationFilter.getValue().getId();
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox(12);
        card.getStyleClass().add("vehicle-card");
        card.setPrefWidth(285);
        card.setMinWidth(260);

        StackPane visual = createVehicleVisual(vehicle);

        Label typeBadge = new Label(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getLabel() : "Vehicle");
        typeBadge.getStyleClass().add("vehicle-type-badge");
        StackPane.setAlignment(typeBadge, Pos.TOP_LEFT);
        StackPane.setMargin(typeBadge, new Insets(12));
        visual.getChildren().add(typeBadge);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.TOP_LEFT);
        Label name = new Label(vehicle.getMake() + " " + vehicle.getModel());
        name.getStyleClass().add("vehicle-card-title");
        name.setWrapText(true);
        HBox.setHgrow(name, Priority.ALWAYS);
        Label price = new Label(String.format("$%.0f/day", vehicle.getPricePerDay()));
        price.getStyleClass().add("vehicle-price");
        titleRow.getChildren().addAll(name, price);

        Label location = new Label(vehicle.getLocationName() != null ? vehicle.getLocationName() : "Location available");
        location.getStyleClass().add("vehicle-card-location");

        HBox meta = new HBox(8);
        meta.getStyleClass().add("vehicle-meta");
        meta.getChildren().addAll(
                createMetaChip(vehicle.getPassengerCapacity() + " seats"),
                createMetaChip(vehicle.getTransmissionType() != null ? vehicle.getTransmissionType().getLabel() : "Auto"),
                createMetaChip(vehicle.getFuelType() != null ? vehicle.getFuelType().getLabel() : "Fuel"));

        HBox actions = new HBox(10);
        actions.getStyleClass().add("vehicle-card-actions");
        Button detailsBtn = new Button("Details");
        detailsBtn.getStyleClass().add("vehicle-outline-btn");
        detailsBtn.setOnAction(e -> {
            if (onViewChange != null) {
                onViewChange.accept(new VehicleDetailView(currentUser, vehicle, onViewChange));
            } else {
                showDetailsDialog(vehicle);
            }
        });
        Button reserveBtn = new Button("Reserve now");
        reserveBtn.getStyleClass().add("vehicle-reserve-btn");
        reserveBtn.setOnAction(e -> {
            if (onViewChange != null) {
                onViewChange.accept(new ReservationView(currentUser, vehicle, onViewChange));
            }
        });
        actions.getChildren().addAll(detailsBtn, reserveBtn);

        card.getChildren().addAll(visual, titleRow, location, meta, actions);
        return card;
    }

    private StackPane createVehicleVisual(Vehicle vehicle) {
        StackPane visual = new StackPane();
        visual.getStyleClass().add("vehicle-card-image-wrap");
        visual.setPrefHeight(150);

        if (vehicle.getImagePath() != null && !vehicle.getImagePath().isBlank()) {
            try {
                ImageView imageView = new ImageView(new Image(vehicle.getImagePath(), 290, 150, true, true, true));
                imageView.getStyleClass().add("vehicle-card-image");
                imageView.setFitWidth(290);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                visual.getChildren().add(imageView);
                return visual;
            } catch (Exception ignored) {
                // Fall through to the branded placeholder when a saved image path is invalid.
            }
        }

        VBox placeholder = new VBox(4);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getStyleClass().add("vehicle-card-placeholder");
        Label make = new Label(vehicle.getMake() != null && !vehicle.getMake().isBlank() ? vehicle.getMake() : "Rental");
        make.getStyleClass().add("vehicle-placeholder-make");
        Label model = new Label(vehicle.getModel() != null && !vehicle.getModel().isBlank() ? vehicle.getModel() : "Automobile");
        model.getStyleClass().add("vehicle-placeholder-model");
        placeholder.getChildren().addAll(make, model);
        visual.getChildren().add(placeholder);
        return visual;
    }

    private Label createMetaChip(String text) {
        Label chip = new Label(text);
        chip.getStyleClass().add("vehicle-chip");
        return chip;
    }

    private VBox createEmptyCatalogState() {
        VBox empty = new VBox(8);
        empty.getStyleClass().add("vehicle-empty-state");
        empty.setAlignment(Pos.CENTER);
        empty.setPrefWidth(720);
        Label title = new Label("No cars match these filters");
        title.getStyleClass().add("vehicle-empty-title");
        Label body = new Label("Try clearing one filter or searching a different model name.");
        body.getStyleClass().add("vehicle-empty-body");
        Button clear = new Button("Clear filters");
        clear.getStyleClass().add("vehicle-secondary-btn");
        clear.setOnAction(e -> {
            memberSearchField.clear();
            memberTypeFilter.setValue(null);
            memberClassFilter.setValue(null);
            memberTransmissionFilter.setValue(null);
            memberFuelFilter.setValue(null);
            memberLocationFilter.setValue(null);
            applyMemberFilters();
        });
        empty.getChildren().addAll(title, body, clear);
        return empty;
    }

    private void setupManagementUI() {
        getStyleClass().add("backend-inventory-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("OPERATIONS");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("Vehicle Inventory");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Manage fleet records, locations, parking stalls, technical status, and vehicle history.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        backendInventorySummaryLabel.getStyleClass().add("backend-inventory-summary");
        hero.getChildren().addAll(eyebrow, title, subtitle, backendInventorySummaryLabel);

        GridPane filterGrid = new GridPane();
        filterGrid.getStyleClass().add("backend-filter-panel");
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(16));

        ComboBox<VehicleType> typeFilter = new ComboBox<>(FXCollections.observableArrayList(VehicleType.values()));
        typeFilter.setPromptText("Type");
        typeFilter.setPrefWidth(120);
        typeFilter.getStyleClass().add("backend-input");

        ComboBox<VehicleStatus> statusFilter = new ComboBox<>(
                FXCollections.observableArrayList(VehicleStatus.values()));
        statusFilter.setPromptText("Status");
        statusFilter.setPrefWidth(120);
        statusFilter.getStyleClass().add("backend-input");

        ComboBox<Location> locationFilter = new ComboBox<>();
        locationFilter.setPromptText("Location");
        locationFilter.getItems().addAll(locationRepo.findAll());
        locationFilter.setPrefWidth(150);
        locationFilter.getStyleClass().add("backend-input");

        ComboBox<RentalSystem> systemFilter = new ComboBox<>();
        systemFilter.setPromptText("System");
        systemFilter.getItems().addAll(systemRepo.findAll());
        systemFilter.setPrefWidth(150);
        systemFilter.getStyleClass().add("backend-input");

        TextField modelSearch = new TextField();
        modelSearch.setPromptText("Search model...");
        modelSearch.getStyleClass().add("backend-text-input");

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("backend-primary-btn");
        searchBtn.setOnAction(e -> {
            List<Vehicle> results = vehicleRepo.search(
                    typeFilter.getValue(),
                    statusFilter.getValue(),
                    modelSearch.getText(),
                    locationFilter.getValue() != null ? locationFilter.getValue().getId() : null,
                    systemFilter.getValue() != null ? systemFilter.getValue().getId() : null);
            table.setItems(FXCollections.observableArrayList(results));
            updateBackendInventorySummary(results);
        });

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("backend-secondary-btn");
        clearBtn.setOnAction(e -> {
            typeFilter.setValue(null);
            statusFilter.setValue(null);
            locationFilter.setValue(null);
            systemFilter.setValue(null);
            modelSearch.clear();
            loadData();
        });

        Label filtersLabel = new Label("Filters");
        filtersLabel.getStyleClass().add("backend-filter-label");
        Label searchLabel = new Label("Search");
        searchLabel.getStyleClass().add("backend-filter-label");

        filterGrid.add(filtersLabel, 0, 0);
        filterGrid.add(typeFilter, 1, 0);
        filterGrid.add(statusFilter, 2, 0);
        filterGrid.add(systemFilter, 3, 0);
        filterGrid.add(locationFilter, 4, 0);

        filterGrid.add(searchLabel, 0, 1);
        filterGrid.add(modelSearch, 1, 1, 2, 1);
        filterGrid.add(searchBtn, 3, 1);
        filterGrid.add(clearBtn, 4, 1);

        // Actions
        HBox actions = new HBox(10);
        actions.getStyleClass().add("backend-action-bar");
        actions.setAlignment(Pos.CENTER_LEFT);
        if (Session.isMember()) {
            Button reserveBtn = new Button("Reserve Selected");
            reserveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            reserveBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null && onViewChange != null) {
                    onViewChange.accept(new ReservationView(currentUser, selected));
                }
            });
            actions.getChildren().add(reserveBtn);
        }

        if (Session.isReceptionist() || Session.isSuperAdmin()) {
            Button historyBtn = new Button("View History");
            historyBtn.getStyleClass().add("backend-secondary-btn");
            historyBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null)
                    showHistoryDialog(selected);
            });
            actions.getChildren().add(historyBtn);
        }

        Button detailsBtn = new Button("View Details");
        detailsBtn.getStyleClass().add("backend-secondary-btn");
        detailsBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showDetailsDialog(selected);
        });
        actions.getChildren().add(detailsBtn);

        Button showBarcodeBtn = new Button("Show QR Code");
        showBarcodeBtn.getStyleClass().add("backend-secondary-btn");
        showBarcodeBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showBarcodeDialog(selected);
        });
        actions.getChildren().add(showBarcodeBtn);

        if (Session.isSuperAdmin() || Session.isReceptionist()) {
            Button addBtn = new Button("Add Vehicle");
            addBtn.getStyleClass().add("backend-primary-btn");
            addBtn.setOnAction(e -> showVehicleDialog(null));

            Button deleteBtn = new Button("Delete Vehicle");
            deleteBtn.getStyleClass().add("backend-danger-btn");
            deleteBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    confirmDeleteVehicle(selected);
                }
            });

            Button reactivateBtn = new Button("Reactivate");
            reactivateBtn.getStyleClass().add("backend-secondary-btn");
            reactivateBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    vehicleRepo.reactivate(selected.getId());
                    loadData();
                }
            });

            Button editBtn = new Button("Edit Vehicle");
            editBtn.getStyleClass().add("backend-secondary-btn");
            editBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null)
                    showVehicleDialog(selected);
            });

            actions.getChildren().addAll(addBtn, editBtn, deleteBtn, reactivateBtn);
        }

        // Table
        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        TableColumn<Vehicle, String> plateCol = new TableColumn<>("Plate #");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        TableColumn<Vehicle, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("locationName"));
        TableColumn<Vehicle, String> stallCol = new TableColumn<>("Stall");
        stallCol.setCellValueFactory(new PropertyValueFactory<>("parkingStallNumber"));
        TableColumn<Vehicle, String> transmissionCol = new TableColumn<>("Transmission");
        transmissionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTransmissionType() != null ? data.getValue().getTransmissionType().getLabel() : ""));
        TableColumn<Vehicle, String> fuelCol = new TableColumn<>("Fuel");
        fuelCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFuelType() != null ? data.getValue().getFuelType().getLabel() : ""));
        TableColumn<Vehicle, Integer> fuelLevelCol = new TableColumn<>("Fuel %");
        fuelLevelCol.setCellValueFactory(new PropertyValueFactory<>("fuelLevel"));
        TableColumn<Vehicle, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Vehicle, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        TableColumn<Vehicle, Double> priceCol = new TableColumn<>("Price/Day");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        table.getColumns().addAll(makeCol, modelCol, plateCol, locCol, stallCol, transmissionCol, fuelCol, fuelLevelCol, priceCol, statusCol, activeCol);
        table.setPlaceholder(new Label("No vehicles found."));
        table.getStyleClass().add("backend-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(hero, filterGrid, actions, table);
    }

    private void showVehicleDialog(Vehicle vehicle) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(10);

        TextField makeField = new TextField(vehicle != null ? vehicle.getMake() : "");
        TextField modelField = new TextField(vehicle != null ? vehicle.getModel() : "");
        TextField plateField = new TextField(vehicle != null ? vehicle.getLicenseNumber() : "");
        TextField barcodeField = new TextField(vehicle != null ? vehicle.getBarcode() : "");
        TextField yearField = new TextField(vehicle != null ? String.valueOf(vehicle.getManufacturingYear()) : "2024");
        TextField mileageField = new TextField(vehicle != null ? String.valueOf(vehicle.getMileage()) : "0");
        TextField capacityField = new TextField(vehicle != null ? String.valueOf(vehicle.getPassengerCapacity()) : "5");
        TextField stockField = new TextField(vehicle != null ? vehicle.getStockNumber() : "");
        TextField imagePathField = new TextField(vehicle != null ? vehicle.getImagePath() : "");
        imagePathField.setEditable(false); // Make it read-only, user must use browse
        Button browseBtn = new Button("Browse...");
        javafx.scene.image.ImageView preview = new javafx.scene.image.ImageView();
        preview.setFitWidth(150);
        preview.setFitHeight(100);
        preview.setPreserveRatio(true);
        preview.setStyle("-fx-border-color: #ccc; -fx-border-style: dashed;");

        if (vehicle != null && vehicle.getImagePath() != null && !vehicle.getImagePath().isBlank()) {
            try {
                preview.setImage(new javafx.scene.image.Image(vehicle.getImagePath()));
            } catch (Exception ex) {
                // Ignore if image fails to load
            }
        }

        browseBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Vehicle Image");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            java.io.File selectedFile = fileChooser.showOpenDialog(dialog);
            if (selectedFile != null) {
                String uri = selectedFile.toURI().toString();
                imagePathField.setText(uri);
                try {
                    preview.setImage(new javafx.scene.image.Image(uri));
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Failed to load selected image.").show();
                }
            }
        });
        TextField fuelLevelField = new TextField(vehicle != null ? String.valueOf(vehicle.getFuelLevel()) : "100");
        CheckBox sunroofCheck = new CheckBox("Has Sunroof");
        if (vehicle != null)
            sunroofCheck.setSelected(vehicle.isHasSunroof());

        TextField priceField = new TextField(vehicle != null ? String.valueOf(vehicle.getPricePerDay()) : "50.0");

        ComboBox<VehicleType> typeBox = new ComboBox<>(FXCollections.observableArrayList(VehicleType.values()));
        if (vehicle != null)
            typeBox.setValue(vehicle.getVehicleType());
        else
            typeBox.setValue(VehicleType.CAR);

        ComboBox<CarType> carTypeBox = new ComboBox<>(FXCollections.observableArrayList(CarType.values()));
        if (vehicle != null)
            carTypeBox.setValue(vehicle.getCarType());
        else
            carTypeBox.setValue(CarType.ECONOMY);

        ComboBox<TransmissionType> transmissionBox = new ComboBox<>(FXCollections.observableArrayList(TransmissionType.values()));
        transmissionBox.setValue(vehicle != null && vehicle.getTransmissionType() != null ? vehicle.getTransmissionType() : TransmissionType.AUTOMATIC);

        ComboBox<FuelType> fuelTypeBox = new ComboBox<>(FXCollections.observableArrayList(FuelType.values()));
        fuelTypeBox.setValue(vehicle != null && vehicle.getFuelType() != null ? vehicle.getFuelType() : FuelType.PETROL);

        ComboBox<Location> locBox = new ComboBox<>(FXCollections.observableArrayList(locationRepo.findAll()));
        ComboBox<ParkingStall> stallBox = new ComboBox<>();
        stallBox.setPromptText("No stall assigned");
        stallBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ParkingStall stall) {
                return stall == null ? "No stall assigned" : stall.toString();
            }

            @Override
            public ParkingStall fromString(String string) {
                return null;
            }
        });
        Label stallHelp = new Label("Choose a location to see open stalls.");
        stallHelp.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Button clearStallBtn = new Button("Clear");
        clearStallBtn.getStyleClass().add("backend-secondary-btn");
        clearStallBtn.setOnAction(e -> stallBox.setValue(null));

        locBox.setOnAction(e -> {
            if (locBox.getValue() != null) {
                List<ParkingStall> availableStalls = new ArrayList<>(stallRepo.findAvailableStalls(locBox.getValue().getId()));
                if (vehicle != null && vehicle.getParkingStallId() != null) {
                    ParkingStall currentStall = stallRepo.findById(vehicle.getParkingStallId());
                    if (currentStall != null && currentStall.getLocationId() == locBox.getValue().getId()
                            && availableStalls.stream().noneMatch(s -> s.getId() == currentStall.getId())) {
                        availableStalls.add(0, currentStall);
                    }
                    stallBox.setValue(currentStall != null && currentStall.getLocationId() == locBox.getValue().getId() ? currentStall : null);
                } else {
                    stallBox.setValue(null);
                }
                stallBox.setItems(FXCollections.observableArrayList(availableStalls));
                if (availableStalls.isEmpty()) {
                    stallHelp.setText("No free stalls at this location. You can save the vehicle without a stall.");
                } else {
                    stallHelp.setText(availableStalls.size() + " free stall(s) available at this location.");
                }
            }
        });

        if (vehicle != null) {
            locBox.getItems().stream().filter(l -> l.getId() == vehicle.getLocationId()).findFirst().ifPresent(l -> {
                locBox.setValue(l);
                locBox.getOnAction().handle(null);
            });
        } else if (!locBox.getItems().isEmpty()) {
            locBox.setValue(locBox.getItems().get(0));
            locBox.getOnAction().handle(null);
        }

        // Layout
        grid.add(new Label("Make:"), 0, 0);
        grid.add(makeField, 1, 0);
        grid.add(new Label("Model:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("License Plate:"), 0, 2);
        grid.add(plateField, 1, 2);
        grid.add(new Label("Barcode:"), 0, 3);
        grid.add(barcodeField, 1, 3);
        grid.add(new Label("Year:"), 2, 0);
        grid.add(yearField, 3, 0);
        grid.add(new Label("Mileage:"), 2, 1);
        grid.add(mileageField, 3, 1);
        grid.add(new Label("Capacity:"), 2, 2);
        grid.add(capacityField, 3, 2);
        grid.add(new Label("Stock #:"), 2, 3);
        grid.add(stockField, 3, 3);
        grid.add(new Label("Vehicle Type:"), 0, 4);
        grid.add(typeBox, 1, 4);
        grid.add(new Label("Car Class:"), 2, 4);
        grid.add(carTypeBox, 3, 4);
        grid.add(new Label("Location:"), 0, 5);
        grid.add(locBox, 1, 5);
        grid.add(new Label("Parking Stall:"), 2, 5);
        VBox stallPicker = new VBox(4, new HBox(8, stallBox, clearStallBtn), stallHelp);
        grid.add(stallPicker, 3, 5);
        grid.add(new Label("Price/Day:"), 0, 6);
        grid.add(priceField, 1, 6);
        grid.add(sunroofCheck, 2, 6);
        grid.add(new Label("Image Path:"), 0, 7);
        HBox imageBox = new HBox(10, imagePathField, browseBtn);
        grid.add(imageBox, 1, 7);
        grid.add(new Label("Preview:"), 0, 9);
        grid.add(preview, 1, 9);
        grid.add(new Label("Transmission:"), 2, 7);
        grid.add(transmissionBox, 3, 7);
        grid.add(new Label("Fuel Type:"), 0, 8);
        grid.add(fuelTypeBox, 1, 8);
        grid.add(new Label("Fuel Level:"), 2, 8);
        grid.add(fuelLevelField, 3, 8);

        Button saveBtn = new Button("Save Vehicle");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setPadding(new Insets(10, 20, 10, 20));

        saveBtn.setOnAction(e -> {
            try {
                Vehicle v = vehicle == null ? new Vehicle() : vehicle;
                v.setMake(makeField.getText());
                v.setModel(modelField.getText());
                v.setLicenseNumber(plateField.getText());
                v.setBarcode(barcodeField.getText());
                v.setManufacturingYear(Integer.parseInt(yearField.getText()));
                v.setMileage(Integer.parseInt(mileageField.getText()));
                v.setPassengerCapacity(Integer.parseInt(capacityField.getText()));
                v.setStockNumber(stockField.getText());
                v.setHasSunroof(sunroofCheck.isSelected());
                v.setVehicleType(typeBox.getValue());
                v.setCarType(carTypeBox.getValue());
                v.setImagePath(imagePathField.getText());
                v.setTransmissionType(transmissionBox.getValue());
                v.setFuelType(fuelTypeBox.getValue());
                v.setFuelLevel(Integer.parseInt(fuelLevelField.getText()));
                if (locBox.getValue() != null)
                    v.setLocationId(locBox.getValue().getId());
                if (stallBox.getValue() != null)
                    v.setParkingStallId(stallBox.getValue().getId());
                else
                    v.setParkingStallId(null);
                v.setPricePerDay(Double.parseDouble(priceField.getText()));

                if (vehicle == null) {
                    v.setStatus(com.cs210.project.constants.VehicleStatus.AVAILABLE);
                    v.setActive(true);
                    vehicleRepo.create(v);
                    logRepo.addLog(v.getId(), VehicleLogType.OTHER, "Full registration completed.",
                            Session.getAccount().getId());
                } else {
                    vehicleRepo.update(v);
                    logRepo.addLog(v.getId(), VehicleLogType.OTHER, "Batch update of technical fields.",
                            Session.getAccount().getId());
                }
                loadData();
                dialog.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Input: " + ex.getMessage()).show();
            }
        });

        VBox root = new VBox(10, grid, saveBtn);
        root.setPadding(new Insets(10));
        root.setAlignment(javafx.geometry.Pos.CENTER);
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private void confirmDeleteVehicle(Vehicle vehicle) {
        if (!vehicle.isActive()) {
            new Alert(Alert.AlertType.INFORMATION, vehicle.getMake() + " " + vehicle.getModel() + " is already inactive.").show();
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deactivate " + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicenseNumber() + ")?",
                ButtonType.YES,
                ButtonType.NO);
        confirm.setTitle("Delete Vehicle");
        confirm.setHeaderText("Confirm vehicle deactivation");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean deleted = vehicleRepo.delete(vehicle.getId());
                loadData();
                if (deleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Vehicle deactivated successfully. Use Reactivate to restore it.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Vehicle could not be deactivated. Please refresh and try again.").show();
                }
            }
        });
    }

    private void showHistoryDialog(Vehicle vehicle) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Vehicle History & Analytics");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setPrefWidth(700);
        layout.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        Label header = new Label(
                vehicle.getMake() + " " + vehicle.getModel() + " [" + vehicle.getLicenseNumber() + "]");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Profit Section (Admins only)
        HBox profitBox = new HBox(10);
        profitBox.setPadding(new Insets(15));
        profitBox.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");

        Label profitLabel = new Label("Total Revenue Generated:");
        profitLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        double profit = vehicleRepo.getVehicleProfit(vehicle.getId());
        Label profitVal = new Label(String.format("$%.2f", profit));
        profitVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        profitBox.getChildren().addAll(profitLabel, profitVal);
        profitBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Tabbed Section for History and Audits
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Usage History
        Tab historyTab = new Tab("Usage History");
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(10));

        TableView<com.cs210.project.models.VehicleReservation> historyTable = new TableView<>();
        TableColumn<com.cs210.project.models.VehicleReservation, String> resCol = new TableColumn<>("Reservation #");
        resCol.setCellValueFactory(new PropertyValueFactory<>("reservationNumber"));
        TableColumn<com.cs210.project.models.VehicleReservation, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        TableColumn<com.cs210.project.models.VehicleReservation, String> pickCol = new TableColumn<>("Picked Up");
        pickCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        TableColumn<com.cs210.project.models.VehicleReservation, String> retCol = new TableColumn<>("Returned");
        retCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        TableColumn<com.cs210.project.models.VehicleReservation, String> staffCol = new TableColumn<>("Received By");
        staffCol.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        TableColumn<com.cs210.project.models.VehicleReservation, Double> amtCol = new TableColumn<>("Revenue");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        historyTable.getColumns().addAll(resCol, memberCol, pickCol, retCol, staffCol, amtCol);
        historyTable.setItems(FXCollections.observableArrayList(vehicleRepo.getVehicleHistoryObjects(vehicle.getId())));
        historyTable.setPrefHeight(300);
        historyBox.getChildren().add(historyTable);
        historyTab.setContent(historyBox);

        // Tab 2: Audit Logs
        Tab auditTab = new Tab("Audit Logs");
        VBox auditBox = new VBox(10);
        auditBox.setPadding(new Insets(10));

        ListView<String> auditList = new ListView<>();
        auditList.setItems(FXCollections.observableArrayList(logRepo.getAuditLogsForVehicle(vehicle.getId())));
        auditList.setPrefHeight(300);
        auditList.setPlaceholder(new Label("No modification logs found."));

        auditBox.getChildren().addAll(new Label("System & Modification History:"), auditList);
        auditTab.setContent(auditBox);

        tabs.getTabs().addAll(historyTab, auditTab);

        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(100);
        closeBtn.setOnAction(e -> dialog.close());
        HBox footer = new HBox(closeBtn);
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        layout.getChildren().addAll(header);
        if (Session.isSuperAdmin()) {
            layout.getChildren().add(profitBox);
        }
        layout.getChildren().addAll(tabs, footer);

        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void showDetailsDialog(Vehicle v) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Vehicle Details: " + v.getMake() + " " + v.getModel());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMinWidth(450);
        layout.setStyle("-fx-background-color: #ffffff;");

        javafx.scene.image.ImageView carImage = new javafx.scene.image.ImageView();
        carImage.setFitWidth(400);
        carImage.setFitHeight(250);
        carImage.setPreserveRatio(true);
        if (v.getImagePath() != null && !v.getImagePath().isBlank()) {
            try {
                carImage.setImage(new javafx.scene.image.Image(v.getImagePath()));
            } catch (Exception ex) {
                // Fallback to placeholder or nothing
            }
        }

        Label header = new Label(v.getMake() + " " + v.getModel());
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane info = new GridPane();
        info.setHgap(20);
        info.setVgap(10);
        info.setStyle("-fx-font-size: 14px;");

        // Helper to add rows
        int row = 0;
        addDetailRow(info, "License Plate:", v.getLicenseNumber(), row++);
        addDetailRow(info, "Barcode:", v.getBarcode() != null ? v.getBarcode() : "N/A", row++);
        addDetailRow(info, "Year:", String.valueOf(v.getManufacturingYear()), row++);
        addDetailRow(info, "Mileage:", String.format("%,d km", v.getMileage()), row++);
        addDetailRow(info, "Capacity:", v.getPassengerCapacity() + " Passengers", row++);
        addDetailRow(info, "Type:", v.getVehicleType().getLabel(), row++);
        addDetailRow(info, "Class:", v.getCarType() != null ? v.getCarType().getLabel() : "N/A", row++);
        addDetailRow(info, "Transmission:", v.getTransmissionType() != null ? v.getTransmissionType().getLabel() : "N/A", row++);
        addDetailRow(info, "Fuel Type:", v.getFuelType() != null ? v.getFuelType().getLabel() : "N/A", row++);
        addDetailRow(info, "Fuel Level:", v.getFuelLevel() + "%", row++);
        addDetailRow(info, "Image:", v.getImagePath() != null && !v.getImagePath().isBlank() ? v.getImagePath() : "Placeholder", row++);
        addDetailRow(info, "Location:", v.getLocationName(), row++);

        String stallName = "Not Assigned";
        if (v.getParkingStallId() != null) {
            com.cs210.project.models.ParkingStall stall = stallRepo.findById(v.getParkingStallId());
            if (stall != null)
                stallName = stall.toString();
        }
        addDetailRow(info, "Parking Stall:", stallName, row++);
        addDetailRow(info, "Price/Day:", String.format("$%.2f", v.getPricePerDay()), row++);
        addDetailRow(info, "Sunroof:", v.isHasSunroof() ? "Yes" : "No", row++);
        addDetailRow(info, "Current Status:", v.getStatus().toString(), row++);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");

        layout.getChildren().addAll(header, new Separator());
        if (carImage.getImage() != null) {
            layout.getChildren().add(carImage);
        }
        layout.getChildren().addAll(info, new Separator(), closeBtn);
        layout.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void addDetailRow(GridPane grid, String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #2c3e50;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private void showBarcodeDialog(Vehicle v) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Vehicle Barcode / QR Code");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Label header = new Label("Vehicle: " + v.getMake() + " " + v.getModel());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        javafx.scene.image.ImageView qrView = new javafx.scene.image.ImageView();
        qrView.setImage(com.cs210.project.utils.BarcodeUtils.generateQRCode(v.getBarcode(), 250, 250));
        
        Label barcodeText = new Label("Barcode Value: " + v.getBarcode());
        barcodeText.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(header, qrView, barcodeText, closeBtn);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void loadData() {
        if (Session.isMember()) {
            memberCatalogVehicles = vehicleRepo.findAll().stream()
                    .filter(this::isMemberVisibleVehicle)
                    .collect(Collectors.toList());
            applyMemberFilters();
        } else {
            List<Vehicle> vehicles = vehicleRepo.findAll();
            table.setItems(FXCollections.observableArrayList(vehicles));
            updateBackendInventorySummary(vehicles);
        }
    }

    private void updateBackendInventorySummary(List<Vehicle> vehicles) {
        if (backendInventorySummaryLabel == null || vehicles == null) {
            return;
        }
        long active = vehicles.stream().filter(Vehicle::isActive).count();
        long available = vehicles.stream().filter(vehicle -> vehicle.getStatus() == VehicleStatus.AVAILABLE).count();
        long loaned = vehicles.stream().filter(vehicle -> vehicle.getStatus() == VehicleStatus.LOANED).count();
        backendInventorySummaryLabel.setText(vehicles.size() + " vehicles · " + active + " active · "
                + available + " available · " + loaned + " loaned");
    }
}
