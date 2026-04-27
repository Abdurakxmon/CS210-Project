package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.models.Payment;
import com.cs210.project.repositories.PaymentRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class PaymentsHistoryView extends VBox {
    private final PaymentRepository paymentRepo = new PaymentRepository();
    private final TableView<Payment> table = new TableView<>();

    public PaymentsHistoryView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("My Payment History");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableColumn<Payment, Integer> idCol = new TableColumn<>("Payment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        TableColumn<Payment, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(column -> new TableCell<Payment, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        TableColumn<Payment, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("paymentType"));

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, dateCol, amountCol, typeCol, statusCol);
        table.setPlaceholder(new Label("No payments found."));

        getChildren().addAll(title, table);
    }

    private void loadData() {
        if (Session.isMember()) {
            table.setItems(FXCollections.observableArrayList(paymentRepo.findByMemberId(Session.getMember().getId())));
        }
    }
}
