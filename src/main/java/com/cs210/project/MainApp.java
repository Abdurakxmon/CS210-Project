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
        primaryStage.setMinWidth(760);
        primaryStage.setMinHeight(620);
        showAuthScene();
    }

    private void applyStyle(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }

    private void showAuthScene() {
        Scene scene = new Scene(new AuthView(this::showDashboardScene, this::showRegistrationScene), 1040, 720);
        applyStyle(scene);
        switchScenePreservingWindow(scene, "CS210 Project | Authentication");
    }

    private void showRegistrationScene() {
        Scene scene = new Scene(new RegistrationView(this::showAuthScene), 1100, 760);
        applyStyle(scene);
        switchScenePreservingWindow(scene, "CS210 Project | Member Registration");
    }

    private void showDashboardScene(Account account) {
        Scene scene = new Scene(new WorkspaceView(this::showAuthScene), 980, 700);
        applyStyle(scene);
        switchScenePreservingWindow(scene, "CS210 Project | Dashboard");
    }

    private void switchScenePreservingWindow(Scene scene, String title) {
        boolean hadScene = primaryStage.getScene() != null;
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        if (hadScene) {
            primaryStage.setWidth(Math.max(currentWidth, primaryStage.getMinWidth()));
            primaryStage.setHeight(Math.max(currentHeight, primaryStage.getMinHeight()));
        } else {
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
        }

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }
}
