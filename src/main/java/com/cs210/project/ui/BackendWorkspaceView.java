package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.ui.backend.AccountManagementView;
import com.cs210.project.ui.backend.LocationManagementView;
import com.cs210.project.ui.backend.ParkingStallManagementView;
import com.cs210.project.ui.backend.PickupReturnView;
import com.cs210.project.ui.backend.RentalSystemManagementView;
import com.cs210.project.ui.backend.SuperAdminDashboardView;
import com.cs210.project.ui.frontend.ReservationsListView;
import com.cs210.project.ui.frontend.VehiclesView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BackendWorkspaceView extends BorderPane {
    private final Runnable onLogout;
    private final Account user;
    private final BorderPane centerHost = new BorderPane();

    public BackendWorkspaceView(Runnable onLogout) {
        this.onLogout = onLogout;
        this.user = Session.getAccount();
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("workspace-root");

        VBox sidebar = createSidebarBase(roleText());
        sidebar.getChildren().add(createUserCard());

        if (Session.isSuperAdmin()) {
            Button dashboardBtn = createSidebarBtn("Dashboard");
            dashboardBtn.setOnAction(e -> showContent(new SuperAdminDashboardView()));
            sidebar.getChildren().addAll(createSectionLabel("Overview"), dashboardBtn);
        }

        Button inventoryBtn = createSidebarBtn("Vehicle Inventory");
        inventoryBtn.setOnAction(e -> showContent(new VehiclesView(user, this::showContent)));

        String reservationsLabel = Session.isWorker() ? "Check Returned Cars" : "All Reservations";
        Button reservationsBtn = createSidebarBtn(reservationsLabel);
        reservationsBtn.setOnAction(e -> showContent(new ReservationsListView()));

        sidebar.getChildren().addAll(createSectionLabel("Operations"), inventoryBtn, reservationsBtn);

        if (!Session.isWorker()) {
            Button stallsBtn = createSidebarBtn("Parking Stalls");
            stallsBtn.setOnAction(e -> showContent(new ParkingStallManagementView()));

            Button pickupReturnBtn = createSidebarBtn("Pickup / Return");
            pickupReturnBtn.setOnAction(e -> showContent(new PickupReturnView()));
            sidebar.getChildren().addAll(stallsBtn, pickupReturnBtn);
        }

        if (Session.isSuperAdmin()) {
            Button accountsBtn = createSidebarBtn("Manage Accounts");
            accountsBtn.setOnAction(e -> showContent(new AccountManagementView()));

            Button systemsBtn = createSidebarBtn("Manage Rental Systems");
            systemsBtn.setOnAction(e -> showContent(new RentalSystemManagementView()));

            Button locationsBtn = createSidebarBtn("Manage Locations");
            locationsBtn.setOnAction(e -> showContent(new LocationManagementView()));

            sidebar.getChildren().addAll(createSectionLabel("Administration"), accountsBtn, systemsBtn, locationsBtn);
        }

        finishSidebar(sidebar);
        ScrollPane sidebarScroll = new ScrollPane(sidebar);
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.getStyleClass().add("workspace-sidebar-scroll");
        setLeft(sidebarScroll);
        centerHost.getStyleClass().add("workspace-center-host");
        setCenter(centerHost);
        showContent(Session.isSuperAdmin() ? new SuperAdminDashboardView() : new VehiclesView(user, this::showContent));
    }

    private void showContent(Node content) {
        BorderPane.setMargin(content, new Insets(0));
        centerHost.setCenter(content);
    }

    private String roleText() {
        if (Session.isSuperAdmin()) {
            return "Super admin console";
        }
        if (Session.isReceptionist()) {
            return "Reception desk";
        }
        return "Worker tools";
    }

    private VBox createSidebarBase(String roleText) {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("workspace-sidebar");
        sidebar.setPadding(new Insets(18));
        sidebar.setPrefWidth(250);

        Label brandLabel = new Label("CS210 Rental");
        brandLabel.getStyleClass().add("workspace-brand");
        Label roleLabel = new Label(roleText);
        roleLabel.getStyleClass().add("workspace-role");
        sidebar.getChildren().addAll(brandLabel, roleLabel);
        return sidebar;
    }

    private VBox createUserCard() {
        VBox userCard = new VBox(4);
        userCard.getStyleClass().add("workspace-user-card");
        Label welcomeLabel = new Label("Hello,");
        welcomeLabel.getStyleClass().add("workspace-user-kicker");
        Label nameLabel = new Label(user.getPerson().getName());
        nameLabel.getStyleClass().add("workspace-user-name");
        nameLabel.setWrapText(true);
        userCard.getChildren().addAll(welcomeLabel, nameLabel);
        return userCard;
    }

    private void finishSidebar(VBox sidebar) {
        Button logoutBtn = createSidebarBtn("Logout");
        logoutBtn.getStyleClass().add("workspace-logout-button");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            onLogout.run();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, logoutBtn);
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
