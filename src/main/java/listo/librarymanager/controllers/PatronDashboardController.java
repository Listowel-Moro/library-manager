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
import listo.librarymanager.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PatronDashboardController {

    @FXML
    private TableView<Book> borrowedBooksTable, searchResultsTable;

    @FXML
    private TextField searchField;

    @FXML
    private Label accountName;

    @FXML
    private Label accountPhone;

    private Patron currentUser;

    @FXML
    private TableColumn<Book, String> titleColumn, publisherColumn, genreColumn, statusColumn, isbnColumn;

    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    public void initialize() {
        currentUser = new Patron("Listowel Adolwin", "020-456-7890", "doe123", false);

        accountName.setText(currentUser.getName());
        accountPhone.setText(currentUser.getPhone());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        loadBooksFromDatabase();
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
        // Simulate some data - ideally, you'd fetch from the database
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

    @FXML
    private void onLogoutClick() {
        // Logic to log out the user and navigate back to the login screen
    }
}
