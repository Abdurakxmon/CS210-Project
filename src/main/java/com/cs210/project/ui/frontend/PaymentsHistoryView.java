package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.constants.Enums.PaymentStatus;
import com.cs210.project.models.Payment;
import com.cs210.project.repositories.PaymentRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentsHistoryView extends VBox {
    private final PaymentRepository paymentRepo = new PaymentRepository();
    private final VBox paymentCards = new VBox(12);
    private final Label summaryLabel = new Label();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public PaymentsHistoryView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        getStyleClass().add("payments-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("payments-hero");
        Label eyebrow = new Label("MEMBER BILLING");
        eyebrow.getStyleClass().add("payments-eyebrow");
        Label title = new Label("My Payments");
        title.getStyleClass().add("payments-title");
        Label subtitle = new Label("Review completed, pending, and failed payments for your reservations.");
        subtitle.getStyleClass().add("payments-subtitle");
        summaryLabel.getStyleClass().add("payments-summary");
        hero.getChildren().addAll(eyebrow, title, subtitle, summaryLabel);

        paymentCards.getStyleClass().add("payments-card-list");
        ScrollPane scrollPane = new ScrollPane(paymentCards);
        scrollPane.getStyleClass().add("payments-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(hero, scrollPane);
    }

    private void loadData() {
        if (!Session.isMember()) {
            return;
        }
        populateCards(paymentRepo.findByMemberId(Session.getMember().getId()));
    }

    private void populateCards(List<Payment> payments) {
        paymentCards.getChildren().clear();
        BigDecimal totalPaid = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.SETTLED)
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summaryLabel.setText(payments.size() + (payments.size() == 1 ? " payment" : " payments")
                + " · $" + String.format("%.2f", totalPaid) + " paid");

        if (payments.isEmpty()) {
            VBox empty = new VBox(8);
            empty.getStyleClass().add("payments-empty");
            empty.setAlignment(Pos.CENTER);
            Label emptyTitle = new Label("No payments yet");
            emptyTitle.getStyleClass().add("payments-empty-title");
            Label emptyBody = new Label("Payments for reservations will appear here after checkout.");
            emptyBody.getStyleClass().add("payments-empty-body");
            empty.getChildren().addAll(emptyTitle, emptyBody);
            paymentCards.getChildren().add(empty);
            return;
        }

        for (Payment payment : payments) {
            paymentCards.getChildren().add(createPaymentCard(payment));
        }
    }

    private VBox createPaymentCard(Payment payment) {
        VBox card = new VBox(12);
        card.getStyleClass().add("payment-card");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBlock = new VBox(4);
        Label amount = new Label(payment.getAmount() == null ? "$0.00" : String.format("$%.2f", payment.getAmount()));
        amount.getStyleClass().add("payment-amount");
        Label id = new Label("Payment #" + payment.getId() + " · Bill #" + payment.getBillId());
        id.getStyleClass().add("payment-id");
        Label reservation = new Label("Reservation " + value(payment.getReservationNumber()) + " · " + value(payment.getVehicleName()));
        reservation.getStyleClass().add("payment-reservation");
        reservation.setWrapText(true);
        titleBlock.getChildren().addAll(amount, id, reservation);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(payment.getStatus() == null ? "Status" : payment.getStatus().getLabel());
        status.getStyleClass().addAll("payment-status-pill", statusStyleClass(payment.getStatus()));
        header.getChildren().addAll(titleBlock, spacer, status);

        HBox details = new HBox(10);
        details.getStyleClass().add("payment-detail-grid");
        details.getChildren().addAll(
                createDetailBlock("Date", payment.getCreationDate() == null ? "N/A" : payment.getCreationDate().format(DATE_FORMAT)),
                createDetailBlock("Reservation", value(payment.getReservationNumber())),
                createDetailBlock("Method", payment.getPaymentType() == null ? "N/A" : payment.getPaymentType().getLabel()),
                createDetailBlock("Status", payment.getStatus() == null ? "N/A" : payment.getStatus().getLabel())
        );

        card.getChildren().addAll(header, details);
        return card;
    }

    private VBox createDetailBlock(String label, String value) {
        VBox block = new VBox(4);
        block.getStyleClass().add("payment-detail-block");
        HBox.setHgrow(block, Priority.ALWAYS);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("payment-detail-label");
        Label valueNode = new Label(value == null || value.isBlank() ? "N/A" : value);
        valueNode.getStyleClass().add("payment-detail-value");
        valueNode.setWrapText(true);
        block.getChildren().addAll(labelNode, valueNode);
        return block;
    }

    private String statusStyleClass(PaymentStatus status) {
        if (status == PaymentStatus.COMPLETED || status == PaymentStatus.SETTLED) {
            return "payment-status-success";
        }
        if (status == PaymentStatus.FAILED || status == PaymentStatus.DECLINED || status == PaymentStatus.CANCELLED) {
            return "payment-status-danger";
        }
        return "payment-status-neutral";
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "N/A" : text;
    }
}
