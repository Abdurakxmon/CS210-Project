package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.repositories.NotificationRepository;
import com.cs210.project.ui.frontend.MemberProfileView;
import com.cs210.project.ui.frontend.NotificationView;
import com.cs210.project.ui.frontend.PaymentsHistoryView;
import com.cs210.project.ui.frontend.ReservationsListView;
import com.cs210.project.ui.frontend.VehiclesView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FrontendWorkspaceView extends BorderPane {
    private final Runnable onLogout;
    private final Account user;

    public FrontendWorkspaceView(Runnable onLogout) {
        this.onLogout = onLogout;
        this.user = Session.getAccount();
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("workspace-root");

        VBox sidebar = createSidebarBase("Member dashboard");
        VBox userCard = createUserCard();
        userCard.getStyleClass().add("workspace-user-card-clickable");
        userCard.setOnMouseClicked(e -> setCenter(new MemberProfileView()));
        sidebar.getChildren().add(userCard);

        Button inventoryBtn = createSidebarBtn("Vehicle Inventory");
        inventoryBtn.setOnAction(e -> setCenter(new VehiclesView(user, this::setCenter)));

        Button myResBtn = createSidebarBtn("My Reservations");
        myResBtn.setOnAction(e -> setCenter(new ReservationsListView()));

        Button paymentsBtn = createSidebarBtn("My Payments");
        paymentsBtn.setOnAction(e -> setCenter(new PaymentsHistoryView()));

        sidebar.getChildren().addAll(
                createSectionLabel("Browse"),
                inventoryBtn,
                createSectionLabel("Member"),
                myResBtn,
                paymentsBtn,
                createNotificationSidebarItem());
        finishSidebar(sidebar);

        setLeft(sidebar);
        setCenter(new VehiclesView(user, this::setCenter));
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

    private StackPane createNotificationSidebarItem() {
        Label badge = new Label(String.valueOf(new NotificationRepository().countUnreadByMemberId(Session.getMember().getId())));
        badge.getStyleClass().add("workspace-notification-badge");
        badge.setVisible(!"0".equals(badge.getText()));
        badge.setManaged(badge.isVisible());

        Button notifyBtn = createSidebarBtn("Notifications");
        notifyBtn.setOnAction(e -> {
            setCenter(new NotificationView());
            badge.setText("0");
            badge.setVisible(false);
            badge.setManaged(false);
        });

        StackPane item = new StackPane(notifyBtn, badge);
        item.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(notifyBtn, Pos.CENTER);
        StackPane.setAlignment(badge, Pos.CENTER_RIGHT);
        StackPane.setMargin(badge, new Insets(0, 12, 0, 0));
        return item;
    }
}
