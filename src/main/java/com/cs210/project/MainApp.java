package com.cs210.project;

import com.cs210.project.models.Account;
import com.cs210.project.ui.frontend.AuthView;
import com.cs210.project.ui.frontend.RegistrationView;
import com.cs210.project.ui.WorkspaceView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

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

    private void applyStyle(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }

    private void showAuthScene() {
        Scene scene = new Scene(new AuthView(this::showDashboardScene, this::showRegistrationScene), 900, 680);
        applyStyle(scene);
        primaryStage.setTitle("CS210 Project | Authentication");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showRegistrationScene() {
        Scene scene = new Scene(new RegistrationView(this::showAuthScene), 900, 680);
        applyStyle(scene);
        primaryStage.setTitle("CS210 Project | Member Registration");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showDashboardScene(Account account) {
        Scene scene = new Scene(new WorkspaceView(this::showAuthScene), 980, 700);
        applyStyle(scene);
        primaryStage.setTitle("CS210 Project | Dashboard");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
