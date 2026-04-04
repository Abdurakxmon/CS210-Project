package com.cs210.project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;

public class HelloController {

    @FXML
    private Label statusLabel;

    @FXML
    protected void onTestConnectionButtonClick() {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                statusLabel.setText("Database Connection: SUCCESS");
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("Database Connection: FAILED");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            statusLabel.setText("Database Connection: ERROR (Check console)");
            statusLabel.setStyle("-fx-text-fill: #b30000; -fx-font-weight: bold;");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCreateVehicleButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("create-vehicle.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);
            Stage stage = new Stage();
            stage.setTitle("Create Vehicle");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
