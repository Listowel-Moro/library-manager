package listo.librarymanager.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import listo.librarymanager.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static listo.librarymanager.config.DatabaseConnection.connectDatabase;
import static listo.librarymanager.utils.HashPassword.hashPassword;


public class AuthController {

    @FXML
    private TextField usernameField;

    @FXML
    public TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button signupButton;

  @FXML
  public void onPatronRegisterButtonClick() {
        String username = usernameField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (username.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        // Save user to database or simulate save
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        boolean isRegisterSuccessful = savePatron(username, phone, hashedPassword);
        if (isRegisterSuccessful) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Signup successful! Please log in.");
            navigateToPatronDashboard();

        } else {
            messageLabel.setText("Signup failed! Username may already exist.");
        }
    }

@FXML
public void onPatronLoginButtonClick() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
        messageLabel.setText("All fields are required!");
        return;
    }

    try (Connection connection = connectDatabase()) {
        String query = "SELECT password FROM patrons WHERE name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String storedHashedPassword = resultSet.getString("password");

            if (BCrypt.checkpw(password, storedHashedPassword)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Login successful!");

                navigateToPatronDashboard();
            } else {
                messageLabel.setText("Login failed! Incorrect password.");
            }
        } else {
            messageLabel.setText("Login failed! User not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        messageLabel.setText("An error occurred. Please try again.");
    }
}


    private boolean savePatron(String username, String phone, String password) {
        // Define the SQL query
        String insertPatronQuery = "INSERT INTO patrons (name, phone, password) VALUES (?, ?, ?)";

        try (Connection connection = connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(insertPatronQuery)) {

            // Set the values for the placeholders
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, password);

            // Execute the update and check if a row was inserted
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if a row was inserted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }


    @FXML
    public void onNavigateToPatronRegisterClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listo/librarymanager/patron-register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle("Patron Register");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current login screen (if necessary)
            Stage currentStage = (Stage) root.getScene().getWindow();
            //currentStage.close();
        } catch (Exception e) {
            System.out.println("Failed to navigate to patron register page" + e);
            //showError("Failed to navigate to Staff Login page.");
        }
    }

    @FXML
    public void onNavigateToPatronLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listo/librarymanager/patron-login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle("Patron Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current login screen (if necessary)
            Stage currentStage = (Stage) root.getScene().getWindow();
            //currentStage.close();
        } catch (Exception e) {
            System.out.println("Failed to navigate to patron login page" + e);
            //showError("Failed to navigate to Patron Login page.");
        }
    }

    @FXML
    public void onNavigateToStaffLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listo/librarymanager/staff-login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle("Staff Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current login screen (if necessary)
            Stage currentStage = (Stage) root.getScene().getWindow();
            //currentStage.close();
        } catch (Exception e) {
            System.out.println("Failed to navigate to staff login page" + e);
            //showError("Failed to navigate to Staff Login page.");
        }
    }

    @FXML
    private void navigateToPatronDashboard() {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listo/librarymanager/patron-dashboard.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dashboard
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle("Patron Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current registration window
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load the dashboard. Please try again.");
        }
    }


    @FXML
    private void navigateToStaffDashboard() {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listo/librarymanager/staff-dashboard.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dashboard
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle("Staff Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current registration window
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load the dashboard. Please try again.");
        }
    }


//    @FXML
//    public void onStaffLoginButtonClick() {
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//        String confirmPassword = confirmPasswordField.getText();
//
//        // Validate input
//        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            messageLabel.setText("All fields are required!");
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            messageLabel.setText("Passwords do not match!");
//            return;
//        }
//
//        // Save user to database or simulate save
//        boolean isSignupSuccessful = saveUser(username, password);
//        if (isSignupSuccessful) {
//            messageLabel.setStyle("-fx-text-fill: green;");
//            messageLabel.setText("Signup successful! Please log in.");
//        } else {
//            messageLabel.setText("Signup failed! Username may already exist.");
//        }
//    }

@FXML
public void onStaffLoginButtonClick() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
        messageLabel.setText("All fields are required!");
        return;
    }

    try (Connection connection = connectDatabase()) {
        String query = "SELECT password FROM staff WHERE name = ? AND isStaff = TRUE";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String storedHashedPassword = resultSet.getString("password");

            if (storedHashedPassword.equals(password)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Login successful!");

                navigateToStaffDashboard();
            } else {
                messageLabel.setText("Login failed! Incorrect password.");
            }
        } else {
            messageLabel.setText("Login failed! Staff not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        messageLabel.setText("An error occurred. Please try again.");
    }
}

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
