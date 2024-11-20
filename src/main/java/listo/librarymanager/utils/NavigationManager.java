package listo.librarymanager.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class NavigationManager {

    private static Stage primaryStage;

    /**
     * Sets the primary stage for the application.
     * This stage will be used for all navigation operations.
     *
     * @param stage the primary stage of the application
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Navigates to a specified FXML view.
     * This method replaces the current scene root with the provided FXML file.
     *
     * @param fxmlPath the relative path to the FXML file to be loaded
     */
    public static void navigateTo(String fxmlPath) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(Objects.requireNonNull(NavigationManager.class.getResource(fxmlPath)));
            // Update the root of the current scene
            primaryStage.getScene().setRoot(root);
        } catch (Exception e) {
            // Print any errors that occur during navigation
            e.printStackTrace();
        }
    }
}
