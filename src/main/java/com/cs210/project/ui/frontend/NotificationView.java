package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.models.Notification;
import com.cs210.project.repositories.NotificationRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class NotificationView extends VBox {
    private final NotificationRepository notifyRepo = new NotificationRepository();
    private final TableView<Notification> table = new TableView<>();

    public NotificationView() {
        setupUI();
        loadData();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableColumn<Notification, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdOn"));

        TableColumn<Notification, String> contentCol = new TableColumn<>("Message");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentCol.setPrefWidth(400);

        table.getColumns().addAll(dateCol, contentCol);

        getChildren().addAll(title, table);
    }

    private void loadData() {
        if (Session.isMember()) {
            List<Notification> list = notifyRepo.findByMemberId(Session.getMember().getId());
            table.setItems(FXCollections.observableArrayList(list));
        }
    }
}
