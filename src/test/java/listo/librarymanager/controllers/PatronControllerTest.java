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

public class PatronControllerTest {
    private static Connection connection;
    private PatronDashboardController patronController;
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
        patronController = spy(new PatronDashboardController());

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
        }).when(patronController).showAlert(any(Alert.AlertType.class), anyString(), anyString());
        doNothing().when(patronController).initialize();
        //doNothing().when(NavigationManager.navigateTo(anyString()));

        authController = new AuthController();
        registerUser();
    }


    @AfterEach
    void clearDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("DELETE FROM patrons");
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
        patronController.searchResultsTable = new TableView<>();
        patronController.bookList.setAll(bookList);

        patronController.loadBooksFromDatabase();

        assertEquals(1, patronController.bookList.size());
        assertEquals("Test Book", patronController.bookList.get(0).getTitle());
    }

    @Test
    void testLoadBooksFromDatabase_NoBook() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        patronController.searchResultsTable = new TableView<>();
        patronController.bookList.setAll(bookList);

        patronController.loadBooksFromDatabase();

        assertEquals(0, patronController.bookList.size());
    }

    @Test
    void testReserveBook_BookNotFound(){
        // Call the reserveBook method with a non-existent ISBN
        Platform.runLater(() -> patronController.reserveBook("9999999999"));

        // Wait for UI update
        //Thread.sleep(1000);

        // No reservation should have been made
        try (var stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM reservations")) {
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                assertEquals(0, resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testReserveBookWithEmptyISBN() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            patronController.reserveBook("");
            latch.countDown();
        });
        latch.await();

        verify(patronController).showAlert(
                eq(Alert.AlertType.WARNING),
                eq("Invalid Input"),
                eq("Please provide a valid ISBN.")
        );
    }

    @Test
    void testReserveBookWhenNoLoggedUser() throws InterruptedException {
        SessionManager.setCurrentUser(null);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            patronController.reserveBook("4389554");
            latch.countDown();
        });
        latch.await();

        verify(patronController).showAlert(
                eq(Alert.AlertType.ERROR),
                eq("User Not Logged In"),
                eq("You need to log in to reserve a book.")
        );
    }

    @Test
    void testReserveBookWhenBookNotAvailable() throws InterruptedException {
        //System.out.println("Current user: " + SessionManager.getCurrentUser().getName());
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            patronController.reserveBook("4389554");
            latch.countDown();
        });
        latch.await();

        verify(patronController).showAlert(
                eq(Alert.AlertType.ERROR),
                eq("Book Not Found"),
                eq("The book with the provided ISBN does not exist.")
        );
    }

    @Test
    void testReserveBookWhenNoCopiesAvailable() throws InterruptedException {
        //System.out.println("Current user: " + SessionManager.getCurrentUser().getName());
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO books (title, publisher, genre, isbn, copies_left) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, "Test Book");
            stmt.setString(2, "Test Publisher");
            stmt.setString(3, "Fiction");
            stmt.setString(4, "1234567890");
            stmt.setInt(5, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            patronController.reserveBook("1234567890");
            latch.countDown();
        });
        latch.await();

        verify(patronController).showAlert(
                eq(Alert.AlertType.WARNING),
                eq("No Copies Available"),
                eq("This book is currently not available. You have been added to the queue.")
        );
    }

    @Test
    void testReserveBookWhenSuccess() throws InterruptedException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO books (title, publisher, genre, isbn, copies_left) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, "Test Book");
            stmt.setString(2, "Test Publisher");
            stmt.setString(3, "Fiction");
            stmt.setString(4, "1234567890");
            stmt.setInt(5, 4);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            patronController.reserveBook("1234567890");
            latch.countDown();
        });
        latch.await();

        verify(patronController).showAlert(
                eq(Alert.AlertType.INFORMATION),
                eq("Success"),
                eq("Book reserved successfully!")
        );
    }

    @Test
    void testPatronLogout(){
        patronController.patronLogout();
        assertNull(SessionManager.getCurrentUser());
    }

//    @Test
//    void testInitialize_ColumnMappings() {
//        patronController.titleColumn = new TableColumn<>();
//        patronController.publisherColumn = new TableColumn<>();
//        patronController.genreColumn = new TableColumn<>();
//        patronController.isbnColumn = new TableColumn<>();
//        patronController.reservationLinkColumn = new TableColumn<>();
//
//        // Call initialize method
//        Platform.runLater(() -> patronController.initialize());
//
//        // Verify column mappings
//        assertEquals("title", patronController.titleColumn.getCellValueFactory().toString());
//        assertEquals("publisher", patronController.publisherColumn.getCellValueFactory().toString());
//        assertEquals("genre", patronController.genreColumn.getCellValueFactory().toString());
//        assertEquals("isbn", patronController.isbnColumn.getCellValueFactory().toString());
//        assertEquals("isbn", patronController.reservationLinkColumn.getCellValueFactory().toString());
//    }


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
