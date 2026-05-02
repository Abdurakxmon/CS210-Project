package com.cs210.project.ui.backend;

import com.cs210.project.models.AdminDashboardStats;
import com.cs210.project.models.AdminMetricRow;
import com.cs210.project.models.AuditLogEntry;
import com.cs210.project.repositories.AdminDashboardRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SuperAdminDashboardView extends VBox {
    private final AdminDashboardRepository dashboardRepo = new AdminDashboardRepository();
    private final GridPane statsGrid = new GridPane();
    private final VBox vehicleRankings = new VBox(10);
    private final VBox workerRankings = new VBox(10);
    private final VBox paymentStaffRankings = new VBox(10);
    private final TableView<AuditLogEntry> auditTable = new TableView<>();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public SuperAdminDashboardView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        getStyleClass().add("backend-reservations-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(8);
        hero.getStyleClass().add("backend-inventory-hero");
        Label eyebrow = new Label("SUPER ADMIN");
        eyebrow.getStyleClass().add("backend-inventory-eyebrow");
        Label title = new Label("Command Dashboard");
        title.getStyleClass().add("backend-inventory-title");
        Label subtitle = new Label("Revenue, fleet activity, best performers, and audit logs for every critical operation.");
        subtitle.getStyleClass().add("backend-inventory-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        Button refreshBtn = new Button("Refresh Dashboard");
        refreshBtn.getStyleClass().add("backend-primary-btn");
        refreshBtn.setOnAction(e -> loadData());

        statsGrid.setHgap(12);
        statsGrid.setVgap(12);

        HBox rankings = new HBox(14);
        rankings.getChildren().addAll(
                createRankingPanel("Most Picked Cars", vehicleRankings),
                createRankingPanel("Best Workers", workerRankings),
                createRankingPanel("Payment Staff", paymentStaffRankings));
        rankings.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));

        setupAuditTable();
        VBox auditPanel = new VBox(10);
        auditPanel.getStyleClass().add("backend-filter-panel");
        auditPanel.setPadding(new Insets(16));
        Label auditTitle = new Label("Audit Trail");
        auditTitle.getStyleClass().add("backend-filter-label");
        auditPanel.getChildren().addAll(auditTitle, auditTable);
        VBox.setVgrow(auditTable, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(new VBox(18, hero, refreshBtn, statsGrid, rankings, auditPanel));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        getChildren().add(scroll);
    }

    private VBox createRankingPanel(String title, VBox content) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("backend-filter-panel");
        panel.setPadding(new Insets(16));
        panel.setMinWidth(240);
        Label label = new Label(title);
        label.getStyleClass().add("backend-filter-label");
        content.setFillWidth(true);
        panel.getChildren().addAll(label, content);
        return panel;
    }

    private void setupAuditTable() {
        TableColumn<AuditLogEntry, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getEventTime() == null ? "" : data.getValue().getEventTime().format(DATE_FORMAT)));
        TableColumn<AuditLogEntry, String> categoryCol = new TableColumn<>("Type");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<AuditLogEntry, String> actorCol = new TableColumn<>("Actor");
        actorCol.setCellValueFactory(new PropertyValueFactory<>("actor"));
        TableColumn<AuditLogEntry, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        TableColumn<AuditLogEntry, String> descCol = new TableColumn<>("Details");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        auditTable.getColumns().addAll(timeCol, categoryCol, actorCol, subjectCol, descCol);
        auditTable.getStyleClass().add("backend-table");
        auditTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        auditTable.setPrefHeight(360);
        auditTable.setPlaceholder(new Label("No audit logs yet."));
    }

    private void loadData() {
        AdminDashboardStats stats = dashboardRepo.getStats();
        statsGrid.getChildren().clear();
        statsGrid.add(createStatCard("Revenue", money(stats.getRevenue()), "Completed and settled payments"), 0, 0);
        statsGrid.add(createStatCard("Outstanding", money(stats.getOutstanding()), "Unpaid balances and post-inspection fees"), 1, 0);
        statsGrid.add(createStatCard("Fleet", String.valueOf(stats.getTotalVehicles()), "Active vehicles"), 2, 0);
        statsGrid.add(createStatCard("Active Rentals", String.valueOf(stats.getActiveReservations()), "Cars currently out"), 0, 1);
        statsGrid.add(createStatCard("Waiting Inspection", String.valueOf(stats.getPendingInspections()), "Worker queue"), 1, 1);
        statsGrid.add(createStatCard("Completed", String.valueOf(stats.getCompletedReservations()), "Returned reservations"), 2, 1);

        populateRanking(vehicleRankings, dashboardRepo.getMostPickedVehicles(), "reservations");
        populateRanking(workerRankings, dashboardRepo.getBestWorkers(), "inspections");
        populateRanking(paymentStaffRankings, dashboardRepo.getTopPaymentStaff(), "payments");
        auditTable.setItems(FXCollections.observableArrayList(dashboardRepo.getAuditLog()));
    }

    private VBox createStatCard(String title, String value, String caption) {
        VBox card = new VBox(7);
        card.getStyleClass().add("backend-filter-panel");
        card.setPadding(new Insets(16));
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("backend-filter-label");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #143326;");
        Label captionLabel = new Label(caption);
        captionLabel.setWrapText(true);
        captionLabel.setStyle("-fx-text-fill: #65776d; -fx-font-size: 12px;");
        card.getChildren().addAll(titleLabel, valueLabel, captionLabel);
        card.setMinWidth(220);
        return card;
    }

    private void populateRanking(VBox container, List<AdminMetricRow> rows, String countLabel) {
        container.getChildren().clear();
        if (rows.isEmpty()) {
            Label empty = new Label("No data yet");
            empty.setStyle("-fx-text-fill: #65776d;");
            container.getChildren().add(empty);
            return;
        }
        int rank = 1;
        for (AdminMetricRow row : rows) {
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            Label number = new Label(String.valueOf(rank++));
            number.setStyle("-fx-background-color: #ffcc4d; -fx-background-radius: 99; -fx-padding: 4 8; -fx-font-weight: bold;");
            VBox text = new VBox(2);
            Label name = new Label(row.getName() == null ? "Unknown" : row.getName());
            name.setWrapText(true);
            name.setStyle("-fx-font-weight: bold; -fx-text-fill: #173629;");
            Label detail = new Label((row.getDetail() == null ? "" : row.getDetail()) + " | " + row.getCount() + " " + countLabel + " | " + row.getAmount());
            detail.setWrapText(true);
            detail.setStyle("-fx-text-fill: #65776d; -fx-font-size: 12px;");
            text.getChildren().addAll(name, detail);
            item.getChildren().addAll(number, text);
            container.getChildren().add(item);
        }
    }

    private String money(BigDecimal value) {
        return "$" + String.format("%.2f", value == null ? BigDecimal.ZERO : value);
    }
}
