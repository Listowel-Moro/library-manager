package listo.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StaffController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button signupButton;

    @FXML
    public void onSignupButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        boolean isSignupSuccessful = saveUser(username, password);
        if (isSignupSuccessful) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Signup successful! Please log in.");
        } else {
            messageLabel.setText("Signup failed! Username may already exist.");
        }
    }


    @FXML
    public void onStaffLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        boolean isSigninSuccessful = saveUser(username, password);
        if (isSigninSuccessful) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Signin successful!");
        } else {
            messageLabel.setText("Signin failed! Wrong credentials entered.");
        }
    }


    private boolean saveUser(String username, String password) {
        // TODO: Replace with actual database save logic
        System.out.printf("Saving user: %s%n", username);
        return true;
    }
}
