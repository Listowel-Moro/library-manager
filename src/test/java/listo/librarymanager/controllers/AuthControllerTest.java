package listo.librarymanager.controllers;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.User;
import listo.librarymanager.utils.DatabaseInitializer;
import listo.librarymanager.utils.JavaFXInitializer;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

class AuthControllerTest {

    private AuthController authController;
    private static Connection connection;

    // Constants for test data
    private static final String VALID_USERNAME = "Listo";
    private static final String VALID_PHONE = "0300000000";
    private static final String VALID_PASSWORD = "password";
    private static final String INVALID_PASSWORD = "wrongPassword";
    static MockedStatic<NavigationManager> mockedNavigationManager = Mockito.mockStatic(NavigationManager.class);


    @BeforeAll
    static void setupAll() throws InterruptedException, SQLException {
        // Start JavaFX runtime
        JavaFXInitializer.initializeJavaFX();

        // Configure H2 database
        DatabaseConnection.setEnvironment("test");
        connection = DatabaseConnection.connectDatabase();
        DatabaseInitializer.initializeDatabase(connection);

        mockedNavigationManager.when(() -> NavigationManager.navigateTo(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0, String.class);
            //System.out.println("Mocked navigateTo called with: " + path);
            return null;
        });
    }

    @BeforeEach
    void setUp() {
        // Initialize AuthController and fields
        authController = new AuthController();
        initializeTextFields();
        initializeMessageLabel();
    }

    @AfterEach
    void clearDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("DELETE FROM patrons;");
            statement.execute("DELETE FROM staff;");
        }
    }

    @AfterAll
    static void tearDownEnd() throws SQLException {
        DatabaseConnection.setEnvironment("production");
        connection.close();
        mockedNavigationManager.close();
    }

    private void initializeTextFields() {
        authController.usernameField = new TextField();
        authController.phoneField = new TextField();
        authController.passwordField = new PasswordField();
        authController.confirmPasswordField = new PasswordField();

        authController.usernameField.setText(VALID_USERNAME);
        authController.phoneField.setText(VALID_PHONE);
        authController.passwordField.setText(VALID_PASSWORD);
        authController.confirmPasswordField.setText(VALID_PASSWORD);
    }

    private void initializeMessageLabel() {
        authController.messageLabel = new Label();
    }

    private void registerValidPatron() {
        authController.onPatronRegisterButtonClick();
        assertEquals("Signup successful! Please log in.", authController.messageLabel.getText());
    }

    private void registerValidStaff() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO staff(name, phone, password, isStaff) VALUES('Listo', '0500000000', 'password', true)");
    }

    @Test
    void emptyRegisterInputValuesShouldPrintErrorMessage() {
        authController.usernameField.setText("");
        authController.onPatronRegisterButtonClick();

        assertNotNull(authController.messageLabel);
        assertEquals("All fields are required!", authController.messageLabel.getText());
    }

    @Test
    void emptyLoginInputValuesShouldPrintErrorMessage() {
        authController.usernameField.setText("");
        authController.onPatronLoginButtonClick();

        assertNotNull(authController.messageLabel);
        assertEquals("All fields are required!", authController.messageLabel.getText());
    }

    @Test
    void mismatchedPasswordShouldPrintErrorMessage() {
        authController.confirmPasswordField.setText("mismatchedPassword");
        authController.onPatronRegisterButtonClick();

        assertEquals("Passwords do not match!", authController.messageLabel.getText());
    }

    @Test
    void testPatronRegistrationSuccess() {
        registerValidPatron();
    }

    @Test
    void loginWithInvalidUsernameShouldReturnUserNotFound() {
        authController.usernameField.setText("nonExistentUser");
        authController.passwordField.setText(VALID_PASSWORD);
        authController.onPatronLoginButtonClick();

        assertEquals("Login failed! User not found.", authController.messageLabel.getText());
    }

    @Test
    void loginWithInvalidPasswordShouldReturnIncorrectPassword() {
        registerValidPatron();

        authController.passwordField.setText(INVALID_PASSWORD);
        authController.onPatronLoginButtonClick();

        assertEquals("Login failed! Incorrect password.", authController.messageLabel.getText());
    }

    @Test
    void testPatronLoginSuccess() {
        registerValidPatron();

        authController.onPatronLoginButtonClick();

        assertEquals("Login successful!", authController.messageLabel.getText());
    }

    @Test
    void staffLoginWithEmptyInputsShouldAlertError(){
        authController.usernameField.setText("");

        authController.onStaffLoginButtonClick();
        assertEquals("All fields are required!", authController.messageLabel.getText());

    }

    @Test
    void loginWithInvalidStaffUsernameShouldAlertError(){
        authController.onStaffLoginButtonClick();
        assertEquals("Login failed! Staff not found.", authController.messageLabel.getText());
    }

    @Test
    void loginWithInvalidStaffPasswordShouldAlertError() throws SQLException {
        registerValidStaff();
        authController.usernameField.setText("Listo");
        authController.passwordField.setText("wrongPassword");

        authController.onStaffLoginButtonClick();
        assertEquals("Login failed! Incorrect password.", authController.messageLabel.getText());
    }

    @Test
    void loggedInStaffShouldHaveSameNameAsCurrentUser() throws SQLException {
        registerValidStaff();

        authController.usernameField.setText("Listo");
        authController.passwordField.setText("password");

        authController.onStaffLoginButtonClick();
        User currentUser = SessionManager.getCurrentUser();
        assertEquals("Listo", currentUser.getName());
        assertEquals("0500000000", currentUser.getPhone());
        assertEquals("Login successful!", authController.messageLabel.getText());
    }

}
