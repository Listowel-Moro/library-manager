package listo.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import listo.librarymanager.models.Patron;
import listo.librarymanager.models.Staff;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;
import lombok.Generated;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static listo.librarymanager.config.DatabaseConnection.connectDatabase;

public class AuthController {

    // FXML fields for user input
    @FXML
    TextField usernameField;
    @FXML public TextField phoneField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;
    @FXML
    Label messageLabel;
    @FXML private Button signupButton;

    /**
     * Called when the Patron registers a new account.
     * It validates the input and saves the Patron to the database.
     */
    @FXML
    public void onPatronRegisterButtonClick() {
        String username = usernameField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input fields
        if (username.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        // Hash the password and save the Patron
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        boolean isRegisterSuccessful = savePatron(username, phone, hashedPassword);
        if (isRegisterSuccessful) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Signup successful! Please log in.");
            onNavigateToPatronLoginClick();
        } else {
            messageLabel.setText("Registration failed. Username and phone might already exist.");
        }
    }

    /**
     * Called when a Patron attempts to log in.
     * It checks the credentials and navigates to the Patron dashboard on success.
     */
    @FXML
    public void onPatronLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        try (Connection connection = connectDatabase()) {
            String query = "SELECT * FROM patrons WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

                // Check password
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Login successful!");

                    Patron loggedInPatron = new Patron(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("phone"),
                            false
                    );

                    // Store Patron in session and navigate to the dashboard
                    SessionManager.setCurrentUser(loggedInPatron);
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

    /**
     * Saves a new Patron to the database.
     *
     * @param username The Patron's username
     * @param phone The Patron's phone number
     * @param password The hashed password
     * @return true if the Patron was saved successfully, false otherwise
     */
    private boolean savePatron(String username, String phone, String password) {
        String insertPatronQuery = "INSERT INTO patrons (name, phone, password) VALUES (?, ?, ?)";

        try (Connection connection = connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(insertPatronQuery)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, password);

            // Execute the update and check if a row was inserted
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Navigates to the Patron registration page.
     */
    @FXML
    @Generated
    public void onNavigateToPatronRegisterClick() {
        try {
            NavigationManager.navigateTo("/listo/librarymanager/patron-register.fxml");
        } catch (Exception e) {
            System.out.println("Failed to navigate to patron register page: " + e);
        }
    }

    /**
     * Navigates to the Patron login page.
     */
    @FXML
    @Generated
    public void onNavigateToPatronLoginClick() {
        try {
            NavigationManager.navigateTo("/listo/librarymanager/patron-login.fxml");
        } catch (Exception e) {
            System.out.println("Failed to navigate to patron login page: " + e);
        }
    }

    /**
     * Navigates to the Staff login page.
     */
    @FXML
    @Generated
    public void onNavigateToStaffLoginClick() {
        try {
            NavigationManager.navigateTo("/listo/librarymanager/staff-login.fxml");
        } catch (Exception e) {
            System.out.println("Failed to navigate to staff login page: " + e);
        }
    }

    /**
     * Navigates to the Patron dashboard page after successful login.
     */
    @FXML
    @Generated
    private void navigateToPatronDashboard() {
        try {
            NavigationManager.navigateTo("/listo/librarymanager/patron-dashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load the dashboard. Please try again.");
        }
    }

    /**
     * Navigates to the Staff dashboard page after successful login.
     */
    @FXML
    @Generated
    private void navigateToStaffDashboard() {
        try {
            NavigationManager.navigateTo("/listo/librarymanager/staff-dashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load the dashboard. Please try again.");
        }
    }

    /**
     * Called when the Staff attempts to log in.
     * It checks the credentials and navigates to the Staff dashboard on success.
     */
    @FXML
    public void onStaffLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        try (Connection connection = connectDatabase()) {
            String query = "SELECT * FROM staff WHERE name = ? AND isStaff = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

                if (password.equals(storedHashedPassword)) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Login successful!");

                    Staff loggedInStaff = new Staff(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("phone"),
                            true
                    );

                    SessionManager.setCurrentUser(loggedInStaff);
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

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message The error message to display
     */
//    private void showError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Error");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}
