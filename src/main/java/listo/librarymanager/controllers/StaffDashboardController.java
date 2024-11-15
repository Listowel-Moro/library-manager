package listo.librarymanager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.Book;
import listo.librarymanager.models.Patron;
import listo.librarymanager.models.Staff;
import listo.librarymanager.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StaffDashboardController {

    @FXML
    private TableView<Book> borrowedBooksTable, searchResultsTable;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Book, String> titleColumn, publisherColumn, genreColumn, statusColumn, isbnColumn;


    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    private Label accountName, accountPhone;

    private Staff currentUser;

    public void initialize() {
        // Example user data - you would typically get this from the logged-in user
        currentUser = new Staff("Listowel Adolwin", "020-456-7890", "doe123", true);

        // Set account details
        accountName.setText(currentUser.getName());
        accountPhone.setText(currentUser.getPhone());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));


        loadBooksFromDatabase();
        // Load borrowed books
        loadBorrowedBooks();
    }

    private void loadBooksFromDatabase() {
        String fetchBooksQuery = "SELECT * FROM books";
        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(fetchBooksQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            bookList.clear(); // Clear any existing books
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("title"),
                        resultSet.getString("publisher"),
                        resultSet.getString("genre"),
                        resultSet.getString("isbn")
                );
                bookList.add(book);
            }
            searchResultsTable.setItems(bookList); // Set items in the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearchClick() {
        loadBooksFromDatabase();
        String searchQuery = searchField.getText().trim().toLowerCase();

        if (searchQuery.isEmpty()) {
            searchResultsTable.setItems(bookList);
            return;
        }

        ObservableList<Book> filteredList = FXCollections.observableArrayList();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(searchQuery) ||
                    book.getPublisher().toLowerCase().contains(searchQuery) ||
                    book.getGenre().toLowerCase().contains(searchQuery) ||
                    book.getIsbn().toLowerCase().contains(searchQuery)) {
                filteredList.add(book);
            }
        }
        searchResultsTable.setItems(filteredList);
    }

    private void loadBorrowedBooks() {
        List<Book> borrowedBooks = currentUser.getBorrowedBooks();  // Get this from the database
        borrowedBooksTable.getItems().setAll(borrowedBooks);
    }

    @FXML
    private void onViewBorrowedBooksClick() {
        loadBorrowedBooks();
    }

    @FXML
    private void onViewProfileClick() {
        // Display user profile information
    }

    // Handle logout
    @FXML
    private void onLogoutClick() {
        // Logic to log out the user and navigate back to the login screen
    }

    @FXML
    private TextField titleField, publisherField, genreField, statusField, isbnField;

    @FXML
    private Label messageLabel;

    @FXML
    public void onAddBookClick() {
        String title = titleField.getText();
        String publisher = publisherField.getText();
        String genre = genreField.getText();
        String status = statusField.getText();
        String isbn = isbnField.getText();

        // Validate input
        if (title.isEmpty() || publisher.isEmpty() || genre.isEmpty() || status.isEmpty() || isbn.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Add book to database
        String insertBookQuery = """
        INSERT INTO books (title, publisher, genre, status, isbn, created_at)
        VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    """;

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(insertBookQuery)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, publisher);
            preparedStatement.setString(3, genre);
            preparedStatement.setString(4, status);
            preparedStatement.setString(5, isbn);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Book added successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Clear input fields
                titleField.clear();
                publisherField.clear();
                genreField.clear();
                statusField.clear();
                isbnField.clear();
            } else {
                messageLabel.setText("Failed to add book. Please try again.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

}
