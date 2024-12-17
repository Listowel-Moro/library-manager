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

    @Override
    public void start(Stage primaryStage) throws Exception {
        NavigationManager.setPrimaryStage(primaryStage);

        Parent root = FXMLLoader.load((Main.class.getResource("login-navigator.fxml")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(600);
        primaryStage.setWidth(800);
        primaryStage.setTitle("Library Manager");
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = connectDatabase();

        if (initializeDatabase(connection)) {
            launch();
        }
    }
}
