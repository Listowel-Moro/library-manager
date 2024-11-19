package listo.librarymanager.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class NavigationManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(NavigationManager.class.getResource(fxmlPath)));
            primaryStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

