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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class VehiclesView extends VBox {
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final RentalSystemRepository systemRepo = new RentalSystemRepository();
    private final VehicleLogRepository logRepo = new VehicleLogRepository();
    private final ParkingStallRepository stallRepo = new ParkingStallRepository();
    private final TableView<Vehicle> table = new TableView<>();
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
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Vehicle Inventory");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Filters organized in a grid to avoid clipping
        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(0, 0, 10, 0));

        ComboBox<VehicleType> typeFilter = new ComboBox<>(FXCollections.observableArrayList(VehicleType.values()));
        typeFilter.setPromptText("Type");
        typeFilter.setPrefWidth(120);

        ComboBox<VehicleStatus> statusFilter = new ComboBox<>(
                FXCollections.observableArrayList(VehicleStatus.values()));
        statusFilter.setPromptText("Status");
        statusFilter.setPrefWidth(120);

        ComboBox<Location> locationFilter = new ComboBox<>();
        locationFilter.setPromptText("Location");
        locationFilter.getItems().addAll(locationRepo.findAll());
        locationFilter.setPrefWidth(150);

        ComboBox<RentalSystem> systemFilter = new ComboBox<>();
        systemFilter.setPromptText("System");
        systemFilter.getItems().addAll(systemRepo.findAll());
        systemFilter.setPrefWidth(150);

        TextField modelSearch = new TextField();
        modelSearch.setPromptText("Search model...");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-font-weight: bold;");
        searchBtn.setOnAction(e -> {
            List<Vehicle> results = vehicleRepo.search(
                    typeFilter.getValue(),
                    statusFilter.getValue(),
                    modelSearch.getText(),
                    locationFilter.getValue() != null ? locationFilter.getValue().getId() : null,
                    systemFilter.getValue() != null ? systemFilter.getValue().getId() : null);
            table.setItems(FXCollections.observableArrayList(results));
        });

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            typeFilter.setValue(null);
            statusFilter.setValue(null);
            locationFilter.setValue(null);
            systemFilter.setValue(null);
            modelSearch.clear();
            loadData();
        });

        filterGrid.add(new Label("Filters:"), 0, 0);
        filterGrid.add(typeFilter, 1, 0);
        filterGrid.add(statusFilter, 2, 0);
        filterGrid.add(systemFilter, 3, 0);
        filterGrid.add(locationFilter, 4, 0);

        filterGrid.add(new Label("Search:"), 0, 1);
        filterGrid.add(modelSearch, 1, 1, 2, 1);
        filterGrid.add(searchBtn, 3, 1);
        filterGrid.add(clearBtn, 4, 1);

        // Actions
        HBox actions = new HBox(10);
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
            historyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            historyBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null)
                    showHistoryDialog(selected);
            });
            actions.getChildren().add(historyBtn);
        }

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        detailsBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showDetailsDialog(selected);
        });
        actions.getChildren().add(detailsBtn);

        Button showBarcodeBtn = new Button("Show QR Code");
        showBarcodeBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        showBarcodeBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showBarcodeDialog(selected);
        });
        actions.getChildren().add(showBarcodeBtn);

        if (Session.isSuperAdmin() || Session.isReceptionist()) {
            Button addBtn = new Button("Add Vehicle");
            addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            addBtn.setOnAction(e -> showVehicleDialog(null));

            Button deleteBtn = new Button("Delete Vehicle");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    vehicleRepo.delete(selected.getId());
                    loadData();
                }
            });

            Button reactivateBtn = new Button("Reactivate");
            reactivateBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
            reactivateBtn.setOnAction(e -> {
                Vehicle selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    vehicleRepo.reactivate(selected.getId());
                    loadData();
                }
            });

            Button editBtn = new Button("Edit Vehicle");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
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

        getChildren().addAll(title, filterGrid, actions, table);
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

        locBox.setOnAction(e -> {
            if (locBox.getValue() != null) {
                stallBox.setItems(
                        FXCollections.observableArrayList(stallRepo.findAvailableStalls(locBox.getValue().getId())));
                if (vehicle != null && vehicle.getParkingStallId() != null) {
                    ParkingStall currentStall = stallRepo.findById(vehicle.getParkingStallId());
                    if (currentStall != null)
                        stallBox.getItems().add(currentStall);
                    stallBox.setValue(currentStall);
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
        grid.add(stallBox, 3, 5);
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
        table.setItems(FXCollections.observableArrayList(vehicleRepo.findAll()));
    }
}
