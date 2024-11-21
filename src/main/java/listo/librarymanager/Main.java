package listo.librarymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import static listo.librarymanager.config.DatabaseConnection.connectDatabase;
import static listo.librarymanager.utils.DatabaseInitializer.initializeDatabase;
import listo.librarymanager.utils.NavigationManager;

public class Main extends Application {
    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs while loading the initial FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the primary stage in the NavigationManager for future navigation
        NavigationManager.setPrimaryStage(primaryStage);

        // Load the initial FXML file (login navigator) and set it to the stage
        Parent root = FXMLLoader.load((Main.class.getResource("login-navigator.fxml")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(600);
        primaryStage.setWidth(800);
        primaryStage.setTitle("Library Manager");
        primaryStage.show();
    }

    /**
     * The main entry point for the application.
     * Establishes the database connection, initializes the database, and launches the JavaFX application.
     *
     * @param args command-line arguments
     * @throws SQLException if an error occurs while connecting to or initializing the database
     */
    public static void main(String[] args) throws SQLException {
        // Establish a database connection
        Connection connection = connectDatabase();

        // Initialize the database and launch the application if initialization succeeds
        if (initializeDatabase(connection)) {
            launch();
        }
    }
}
