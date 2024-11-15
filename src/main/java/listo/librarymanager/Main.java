package listo.librarymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


import static listo.librarymanager.config.DatabaseConnection.connectDatabase;
import static listo.librarymanager.utils.DatabaseInitializer.initializeDatabase;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-navigator.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Library manager!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = connectDatabase();
        if (initializeDatabase(connection)){
        launch();
        } else {
            return;
        }
    }
}