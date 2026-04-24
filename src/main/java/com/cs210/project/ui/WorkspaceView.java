package com.cs210.project.ui;

import com.cs210.project.models.Account;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class WorkspaceView extends VBox {

    private enum Section {
        DASHBOARD,
        VEHICLES,
        ACCOUNTS
    }

    private final VBox contentArea = new VBox(16);
    private final DashboardView dashboardView;
    private final VehiclesView vehiclesView = new VehiclesView();
    private final AccountsView accountsView;

    private Button dashboardButton;
    private Button vehiclesButton;
    private Button accountsButton;

    public WorkspaceView(Account currentAccount, Runnable logoutHandler) {
        this.dashboardView = new DashboardView(currentAccount);
        this.accountsView = new AccountsView(currentAccount);
        buildView(logoutHandler);
        showSection(Section.DASHBOARD);
    }

    private void buildView(Runnable logoutHandler) {
        setPadding(new Insets(24));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f3f6fb, #e8eef6);" +
                        "-fx-text-fill: #1f2a37;" +
                        "-fx-text-background-color: #1f2a37;"
        );

        VBox mainContent = new VBox(16, createTopBar(logoutHandler), contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        HBox shell = new HBox(16, createSidebar(), mainContent);
        shell.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(shell);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    private VBox createSidebar() {
        Label menuLabel = new Label("Menu");
        menuLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #5f6978;");

        dashboardButton = createSidebarButton("Dashboard", () -> showSection(Section.DASHBOARD));
        vehiclesButton = createSidebarButton("Vehicles", () -> showSection(Section.VEHICLES));
        accountsButton = createSidebarButton("Accounts", () -> showSection(Section.ACCOUNTS));

        VBox sidebar = new VBox(10, menuLabel, dashboardButton, vehiclesButton, accountsButton);
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(190);
        sidebar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #d5deeb;" +
                        "-fx-border-radius: 12;"
        );
        return sidebar;
    }

    private Button createSidebarButton(String text, Runnable onClick) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(event -> onClick.run());
        button.setStyle(
                "-fx-background-color: #f2f6fb;" +
                        "-fx-text-fill: #1f2a37;" +
                        "-fx-font-weight: 700;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10 12 10 12;"
        );
        return button;
    }

    private HBox createTopBar(Runnable logoutHandler) {
        Label brandLabel = new Label("CS210 Car Rental");
        brandLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: #1f2a37;");

        Label subtitleLabel = new Label("Simple dashboard");
        subtitleLabel.setStyle("-fx-text-fill: #5f6978;");

        VBox titleBox = new VBox(2, brandLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> logoutHandler.run());
        logoutButton.setStyle("-fx-background-color: #1f4f7a; -fx-text-fill: white; -fx-font-weight: 700;");

        HBox topBar = new HBox(12, titleBox, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14));
        topBar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d5deeb; -fx-border-radius: 12;");
        return topBar;
    }

    private void showSection(Section section) {
        if (section == Section.DASHBOARD) {
            contentArea.getChildren().setAll(dashboardView);
        } else if (section == Section.VEHICLES) {
            contentArea.getChildren().setAll(vehiclesView);
        } else {
            contentArea.getChildren().setAll(accountsView);
        }
        applySidebarState(section);
    }

    private void applySidebarState(Section section) {
        styleSidebarButton(dashboardButton, section == Section.DASHBOARD);
        styleSidebarButton(vehiclesButton, section == Section.VEHICLES);
        styleSidebarButton(accountsButton, section == Section.ACCOUNTS);
    }

    private void styleSidebarButton(Button button, boolean active) {
        button.setStyle(active
                ? "-fx-background-color: #1f4f7a; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 12 10 12;"
                : "-fx-background-color: #f2f6fb; -fx-text-fill: #1f2a37; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 12 10 12;");
    }
}
