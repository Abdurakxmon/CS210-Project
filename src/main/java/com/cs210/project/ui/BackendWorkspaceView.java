package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.ui.backend.AccountManagementView;
import com.cs210.project.ui.backend.LocationManagementView;
import com.cs210.project.ui.backend.ParkingStallManagementView;
import com.cs210.project.ui.backend.PickupReturnView;
import com.cs210.project.ui.backend.RentalSystemManagementView;
import com.cs210.project.ui.frontend.ReservationsListView;
import com.cs210.project.ui.frontend.VehiclesView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BackendWorkspaceView extends BorderPane {
    private final Runnable onLogout;
    private final Account user;

    public BackendWorkspaceView(Runnable onLogout) {
        this.onLogout = onLogout;
        this.user = Session.getAccount();
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("workspace-root");

        VBox sidebar = createSidebarBase(roleText());
        sidebar.getChildren().add(createUserCard());

        Button inventoryBtn = createSidebarBtn("Vehicle Inventory");
        inventoryBtn.setOnAction(e -> setCenter(new VehiclesView(user, this::setCenter)));

        String reservationsLabel = Session.isWorker() ? "Check Returned Cars" : "All Reservations";
        Button reservationsBtn = createSidebarBtn(reservationsLabel);
        reservationsBtn.setOnAction(e -> setCenter(new ReservationsListView()));

        sidebar.getChildren().addAll(createSectionLabel("Operations"), inventoryBtn, reservationsBtn);

        if (!Session.isWorker()) {
            Button stallsBtn = createSidebarBtn("Parking Stalls");
            stallsBtn.setOnAction(e -> setCenter(new ParkingStallManagementView()));

            Button pickupReturnBtn = createSidebarBtn("Pickup / Return");
            pickupReturnBtn.setOnAction(e -> setCenter(new PickupReturnView()));
            sidebar.getChildren().addAll(stallsBtn, pickupReturnBtn);
        }

        if (Session.isSuperAdmin()) {
            Button accountsBtn = createSidebarBtn("Manage Accounts");
            accountsBtn.setOnAction(e -> setCenter(new AccountManagementView()));

            Button systemsBtn = createSidebarBtn("Manage Rental Systems");
            systemsBtn.setOnAction(e -> setCenter(new RentalSystemManagementView()));

            Button locationsBtn = createSidebarBtn("Manage Locations");
            locationsBtn.setOnAction(e -> setCenter(new LocationManagementView()));

            sidebar.getChildren().addAll(createSectionLabel("Administration"), accountsBtn, systemsBtn, locationsBtn);
        }

        finishSidebar(sidebar);
        setLeft(sidebar);
        setCenter(new VehiclesView(user, this::setCenter));
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
