package com.cs210.project;

import com.cs210.project.infrastructure.persistence.AppDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static final String AUTH_VIEW = "/com/cs210/project/auth.fxml";
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static <T> T showScene(String resourcePath, String title, double width, double height) throws IOException {
        URL location = MainApp.class.getResource(resourcePath);
        if (location == null) {
            throw new IOException("Could not load FXML resource: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        Scene scene = new Scene(loader.load(), width, height);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        return loader.getController();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(700);
        showScene(AUTH_VIEW, "CS210 Project | Authentication", 1120, 720);
    }

    @Override
    public void stop() {
        AppDatabase.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}
