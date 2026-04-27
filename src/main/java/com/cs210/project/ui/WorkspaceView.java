package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WorkspaceView extends BorderPane {
    private final Runnable onLogout;

    public WorkspaceView(Runnable onLogout) {
        this.onLogout = onLogout;
        setupUI();
    }

    private void setupUI() {
        Account user = Session.getAccount();
        
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label welcomeLabel = new Label("Hello, " + user.getPerson().getName());
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");

        Button inventoryBtn = createSidebarBtn("Vehicle Inventory");
        inventoryBtn.setOnAction(e -> setCenter(new VehiclesView(user, this::setCenter)));

        sidebar.getChildren().addAll(welcomeLabel, inventoryBtn);

        if (Session.isMember()) {
            Button myResBtn = createSidebarBtn("My Reservations");
            myResBtn.setOnAction(e -> setCenter(new ReservationsListView()));

            Button reserveBtn = createSidebarBtn("Make Reservation");
            reserveBtn.setOnAction(e -> setCenter(new ReservationView(user)));

            Button notifyBtn = createSidebarBtn("Notifications");
            notifyBtn.setOnAction(e -> setCenter(new NotificationView()));
            
            Button paymentsBtn = createSidebarBtn("My Payments");
            paymentsBtn.setOnAction(e -> setCenter(new PaymentsHistoryView()));

            Button memberPickupBtn = createSidebarBtn("Pickup / Return");
            memberPickupBtn.setOnAction(e -> setCenter(new PickupReturnView()));

            sidebar.getChildren().addAll(myResBtn, reserveBtn, notifyBtn, paymentsBtn, memberPickupBtn);
        }

        if (Session.isReceptionist() || Session.isSuperAdmin() || Session.isWorker()) {
            String resLabel = Session.isWorker() ? "Check Returned Cars" : "All Reservations";
            Button allResBtn = createSidebarBtn(resLabel);
            allResBtn.setOnAction(e -> setCenter(new ReservationsListView()));
            
            sidebar.getChildren().add(allResBtn);
            if (!Session.isWorker()) {
                Button staffPickupBtn = createSidebarBtn("Pickup / Return");
                staffPickupBtn.setOnAction(e -> setCenter(new PickupReturnView()));
                
                Button stallsBtn = createSidebarBtn("Parking Stalls");
                stallsBtn.setOnAction(e -> setCenter(new ParkingStallManagementView()));
                sidebar.getChildren().addAll(stallsBtn, staffPickupBtn);
            }
        }

        if (Session.isSuperAdmin()) {
            Button accountsBtn = createSidebarBtn("Manage Accounts");
            accountsBtn.setOnAction(e -> setCenter(new AccountManagementView()));
            
            Button systemsBtn = createSidebarBtn("Manage Rental Systems");
            systemsBtn.setOnAction(e -> setCenter(new RentalSystemManagementView()));
            
            Button locationsBtn = createSidebarBtn("Manage Locations");
            locationsBtn.setOnAction(e -> setCenter(new LocationManagementView()));

            sidebar.getChildren().addAll(accountsBtn, systemsBtn, locationsBtn);
        }

        Button logoutBtn = createSidebarBtn("Logout");
        logoutBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            onLogout.run();
        });

        sidebar.getChildren().add(logoutBtn);
        setLeft(sidebar);

        // Default center
        setCenter(new VehiclesView(user, this::setCenter));
    }

    private Button createSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        return btn;
    }
}
