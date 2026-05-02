package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.services.SystemTaskService;
import com.cs210.project.ui.backend.*;
import com.cs210.project.ui.frontend.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class WorkspaceView extends BorderPane {
    private final Runnable onLogout;

    public WorkspaceView(Runnable onLogout) {
        this.onLogout = onLogout;
        setupUI();
    }

    private void setupUI() {
        Account user = Session.getAccount();
        new SystemTaskService().runSystemTasks();

        getStyleClass().add("workspace-root");

        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("workspace-sidebar");
        sidebar.setPadding(new Insets(18));
        sidebar.setPrefWidth(250);

        Label brandLabel = new Label("CS210 Rental");
        brandLabel.getStyleClass().add("workspace-brand");
        Label roleLabel = new Label(Session.isSuperAdmin() ? "Super admin console" : Session.isReceptionist() ? "Reception desk" : Session.isWorker() ? "Worker tools" : "Member dashboard");
        roleLabel.getStyleClass().add("workspace-role");

        VBox userCard = new VBox(4);
        userCard.getStyleClass().add("workspace-user-card");
        Label welcomeLabel = new Label("Hello,");
        welcomeLabel.getStyleClass().add("workspace-user-kicker");
        Label nameLabel = new Label(user.getPerson().getName());
        nameLabel.getStyleClass().add("workspace-user-name");
        nameLabel.setWrapText(true);
        userCard.getChildren().addAll(welcomeLabel, nameLabel);

        Button inventoryBtn = createSidebarBtn("Vehicle Inventory");
        inventoryBtn.setOnAction(e -> setCenter(new VehiclesView(user, this::setCenter)));

        sidebar.getChildren().addAll(brandLabel, roleLabel, userCard, createSectionLabel("Browse"), inventoryBtn);

        if (Session.isMember()) {
            Button myResBtn = createSidebarBtn("My Reservations");
            myResBtn.setOnAction(e -> setCenter(new ReservationsListView()));

            Button reserveBtn = createSidebarBtn("Make Reservation");
            reserveBtn.setOnAction(e -> setCenter(new ReservationView(user, null, this::setCenter)));

            Button notifyBtn = createSidebarBtn("Notifications");
            notifyBtn.setOnAction(e -> setCenter(new NotificationView()));
            
            Button paymentsBtn = createSidebarBtn("My Payments");
            paymentsBtn.setOnAction(e -> setCenter(new PaymentsHistoryView()));

            sidebar.getChildren().addAll(createSectionLabel("Member"), reserveBtn, myResBtn, paymentsBtn, notifyBtn);
        }

        if (Session.isReceptionist() || Session.isSuperAdmin() || Session.isWorker()) {
            String resLabel = Session.isWorker() ? "Check Returned Cars" : "All Reservations";
            Button allResBtn = createSidebarBtn(resLabel);
            allResBtn.setOnAction(e -> setCenter(new ReservationsListView()));
            
            sidebar.getChildren().addAll(createSectionLabel("Operations"), allResBtn);
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
        logoutBtn.getStyleClass().add("workspace-logout-button");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            onLogout.run();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        sidebar.getChildren().add(logoutBtn);
        setLeft(sidebar);

        // Default center
        setCenter(new VehiclesView(user, this::setCenter));
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("workspace-section-label");
        return label;
    }

    private Button createSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("workspace-nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }
}
