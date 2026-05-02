package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.models.Notification;
import com.cs210.project.repositories.NotificationRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationView extends VBox {
    private final NotificationRepository notifyRepo = new NotificationRepository();
    private final VBox notificationCards = new VBox(12);
    private final Label summaryLabel = new Label();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public NotificationView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        getStyleClass().add("notifications-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("notifications-hero");
        Label eyebrow = new Label("MEMBER UPDATES");
        eyebrow.getStyleClass().add("notifications-eyebrow");
        Label title = new Label("Notifications");
        title.getStyleClass().add("notifications-title");
        Label subtitle = new Label("Reservation confirmations, pickup reminders, return updates, and billing notices.");
        subtitle.getStyleClass().add("notifications-subtitle");
        summaryLabel.getStyleClass().add("notifications-summary");
        hero.getChildren().addAll(eyebrow, title, subtitle, summaryLabel);

        notificationCards.getStyleClass().add("notifications-card-list");
        ScrollPane scrollPane = new ScrollPane(notificationCards);
        scrollPane.getStyleClass().add("notifications-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(hero, scrollPane);
    }

    private void loadData() {
        if (!Session.isMember()) {
            return;
        }

        List<Notification> notifications = notifyRepo.findByMemberId(Session.getMember().getId());
        populateCards(notifications);
        notifyRepo.markByMemberIdAsRead(Session.getMember().getId());
    }

    private void populateCards(List<Notification> notifications) {
        notificationCards.getChildren().clear();
        long unreadCount = notifications.stream().filter(notification -> !notification.isRead()).count();
        summaryLabel.setText(notifications.size() + (notifications.size() == 1 ? " notification" : " notifications")
                + " · " + unreadCount + " unread");

        if (notifications.isEmpty()) {
            VBox empty = new VBox(8);
            empty.getStyleClass().add("notifications-empty");
            empty.setAlignment(Pos.CENTER);
            Label emptyTitle = new Label("No notifications yet");
            emptyTitle.getStyleClass().add("notifications-empty-title");
            Label emptyBody = new Label("Reservation updates and reminders will appear here.");
            emptyBody.getStyleClass().add("notifications-empty-body");
            empty.getChildren().addAll(emptyTitle, emptyBody);
            notificationCards.getChildren().add(empty);
            return;
        }

        for (Notification notification : notifications) {
            notificationCards.getChildren().add(createNotificationCard(notification));
        }
    }

    private VBox createNotificationCard(Notification notification) {
        VBox card = new VBox(10);
        card.getStyleClass().add("notification-card");
        if (!notification.isRead()) {
            card.getStyleClass().add("notification-card-unread");
        }

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label type = new Label(notification.getNotificationType() == null ? "System" : notification.getNotificationType().getLabel());
        type.getStyleClass().add("notification-type");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(notification.isRead() ? "Read" : "Unread");
        status.getStyleClass().add(notification.isRead() ? "notification-read-pill" : "notification-unread-pill");
        header.getChildren().addAll(type, spacer, status);

        Label message = new Label(notification.getContent());
        message.getStyleClass().add("notification-message");
        message.setWrapText(true);

        Label date = new Label(notification.getCreatedOn() == null ? "Date unavailable" : notification.getCreatedOn().format(DATE_FORMAT));
        date.getStyleClass().add("notification-date");

        card.getChildren().addAll(header, message, date);
        return card;
    }
}
