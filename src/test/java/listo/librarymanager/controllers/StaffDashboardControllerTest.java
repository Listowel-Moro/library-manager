package listo.librarymanager.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.Book;
import listo.librarymanager.models.Patron;
import listo.librarymanager.utils.DatabaseInitializer;
import listo.librarymanager.utils.JavaFXInitializer;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
//import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static listo.librarymanager.config.DatabaseConnection.connectDatabase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StaffDashboardControllerTest {
    private static Connection connection;
    private static StaffDashboardController staffController;
    private AuthController authController;
    static MockedStatic<NavigationManager> mockedNavigationManager = Mockito.mockStatic(NavigationManager.class);

    @BeforeAll
    static void setUpAll() throws SQLException, InterruptedException {
        DatabaseConnection.setEnvironment("test");
        connection = DatabaseConnection.connectDatabase();

        // Start JavaFX runtime
        JavaFXInitializer.initializeJavaFX();

        // Initialize H2 database
        DatabaseInitializer.initializeDatabase(connection);

        mockedNavigationManager.when(() -> NavigationManager.navigateTo(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0, String.class);
            //System.out.println("Mocked navigateTo called with: " + path);
            return null;
        });
    }

    @BeforeEach
    void setUp() {
        staffController = spy(new StaffDashboardController());

        // Mock the showAlert method to capture calls
        doAnswer(invocation -> {
            Alert.AlertType alertType = invocation.getArgument(0);
            String title = invocation.getArgument(1);
            String message = invocation.getArgument(2);

            System.out.println("showAlert called with: " +
                    "Type: " + alertType + ", " +
                    "Title: " + title + ", " +
                    "Message: " + message);

            // Simulate Alert behavior
            return null;
        }).when(staffController).showAlert(any(Alert.AlertType.class), anyString(), anyString());
        doNothing().when(staffController).initialize();
        //doNothing().when(NavigationManager.navigateTo(anyString()));

        authController = new AuthController();
        registerUser();
        initializeFields();
    }


    @AfterEach
    void clearDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("DELETE FROM staff");
            statement.execute("DELETE FROM books;");
            statement.execute("DELETE FROM reservations;");
        }

        //SessionManager.setCurrentUser(null);
    }

    @AfterAll
    static void tearDownEnd() throws SQLException {
        DatabaseConnection.setEnvironment("production");
        connection.close();
        mockedNavigationManager.close();
    }

    @Test
    void testLoadBooksFromDatabase() throws SQLException, InterruptedException {
        // Insert mock data
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO books (title, publisher, genre, isbn, copies_left) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, "Test Book");
            stmt.setString(2, "Test Publisher");
            stmt.setString(3, "Fiction");
            stmt.setString(4, "1234567490");
            stmt.setInt(5, 5);
            stmt.executeUpdate();
        }

        // Call the method
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        staffController.searchResultsTable = new TableView<>();
        staffController.bookList.setAll(bookList);

        staffController.loadBooksFromDatabase();

        assertEquals(1, staffController.bookList.size());
        assertEquals("Test Book", staffController.bookList.get(0).getTitle());
    }

    @Test
    void testLoadBooksFromDatabase_NoBook() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        staffController.searchResultsTable = new TableView<>();
        staffController.bookList.setAll(bookList);

        staffController.loadBooksFromDatabase();

        assertEquals(0, staffController.bookList.size());
    }

    @Test
    void testStaffLogout(){
        staffController.staffLogout();
        assertNull(SessionManager.getCurrentUser());
    }

    @Test
    void testAddBookWihInvalidCopiesLeft(){
        staffController.copiesLeftField.setText("invalidCopiesLeft");
        staffController.onAddBookClick();
        //staffController.onAddBookClick();
        assertEquals("Invalid Copies left", staffController.messageLabel.getText());
        //assertThrows(NumberFormatException.class, staffController::onAddBookClick);
    }

    @Test
    void testAddBookWithEmptyInputs(){
        staffController.onAddBookClick();
        assertEquals("All fields are required!", staffController.messageLabel.getText());
    }

    @Test
    void testAddBookWithValidInputs(){
        staffController.titleField.setText("title");
        staffController.copiesLeftField.setText("5");
        staffController.isbnField.setText("isbn");
        staffController.publisherField.setText("publisher");
        staffController.genreField.setText("genre");
        staffController.onAddBookClick();
        assertEquals("Book added successfully!", staffController.messageLabel.getText());
    }

    @Test
    void testAddBookWithNoTableCreated(){
        try (var statement = connection.createStatement()) {
            statement.execute("TRUNCATE Table books");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        staffController.titleField.setText("title");
        staffController.copiesLeftField.setText("5");
        staffController.isbnField.setText("isbn");
        staffController.publisherField.setText("publisher");
        staffController.genreField.setText("genre");
        staffController.onAddBookClick();
        assertEquals("Database error: ", staffController.messageLabel.getText());
    }

    public static void initializeFields(){
        staffController.titleField = new TextField();
        staffController.copiesLeftField = new TextField();
        staffController.publisherField = new TextField();
        staffController.genreField = new TextField();
        staffController.isbnField = new TextField();
        staffController.messageLabel = new Label();
    }

    public void registerUser() {
        authController.usernameField = new TextField();
        authController.phoneField = new TextField();
        authController.passwordField = new PasswordField();
        authController.confirmPasswordField = new PasswordField();
        authController.messageLabel = new Label();

        authController.usernameField.setText("Listo");
        authController.phoneField.setText("0500000000");
        authController.passwordField.setText("password");
        authController.confirmPasswordField.setText("password");

        authController.onPatronRegisterButtonClick();
        authController.onPatronLoginButtonClick();
    }


}
