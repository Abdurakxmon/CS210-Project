package com.cs210.project;

import com.cs210.project.config.AppDatabase;
import com.cs210.project.controllers.AuthController;
import com.cs210.project.models.Account;
import com.cs210.project.ui.AuthView;
import com.cs210.project.ui.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private final AuthController authController = new AuthController();

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(700);
        showAuthScene();
    }

    @Override
    public void stop() {
        AppDatabase.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }

    private void showAuthScene() {
        Scene scene = new Scene(new AuthView(authController, this::showDashboardScene), 900, 680);
        primaryStage.setTitle("CS210 Project | Authentication");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showDashboardScene(Account account) {
        Scene scene = new Scene(new DashboardView(account, this::showAuthScene), 980, 700);
        primaryStage.setTitle("CS210 Project | Dashboard");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
