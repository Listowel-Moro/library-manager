package listo.librarymanager.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.Book;
import listo.librarymanager.models.Reservation;
import listo.librarymanager.models.Staff;
import listo.librarymanager.models.User;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;

import java.sql.*;
import java.util.List;

public class StaffDashboardController {

    @FXML
    private TableView<Book> borrowedBooksTable, searchResultsTable;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Book, String> titleColumn, publisherColumn, genreColumn, isbnColumn, copiesLeftColumn;


    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    private Label accountName, accountPhone;

    private Staff currentUser;

    public void initialize() {
        currentUser = (Staff) SessionManager.getCurrentUser();
        accountName.setText(currentUser.getName());
        accountPhone.setText(currentUser.getPhone());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        copiesLeftColumn.setCellValueFactory(new PropertyValueFactory<>("copiesLeft"));

        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patronNameColumn.setCellValueFactory(new PropertyValueFactory<>("patronName"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        hasBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("hasBorrowed"));
        approveColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        approveColumn.setCellFactory(column -> new TableCell<Reservation, Integer>() {
            private final Hyperlink link = new Hyperlink("Approve");

            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    link.setOnAction(event -> {
                        handleApproval(id);
                    });
                    setGraphic(link);
                    setText(null);
                }
            }
        });


        loadBooksFromDatabase();
        loadPendingReservations();
    }

    private void loadBooksFromDatabase() {
        String fetchBooksQuery = "SELECT * FROM books";
        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(fetchBooksQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            bookList.clear();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("title"),
                        resultSet.getString("publisher"),
                        resultSet.getString("genre"),
                        resultSet.getString("isbn"),
                        resultSet.getInt("copies_left")
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

    @FXML
    public void staffLogout(){
        SessionManager.clearSession();
        NavigationManager.navigateTo("/listo/librarymanager/login-navigator.fxml");
    }

    @FXML
    private TextField titleField, publisherField, genreField, copiesLeftField, isbnField;

    @FXML
    private Label messageLabel;

    @FXML
    public void onAddBookClick() {
        String title = titleField.getText();
        String publisher = publisherField.getText();
        String genre = genreField.getText();
        //int copiesLeft = Integer.parseInt(copiesLeftField.getText());
        String isbn = isbnField.getText();
        int copiesLeft;
        try {
            String input = copiesLeftField.getText().trim();
            if (input.isEmpty()) {
                copiesLeft = 1;
            } else {
                copiesLeft = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            copiesLeft = 1;
            System.out.println("Invalid input for copiesLeft. Defaulting to 1.");
        }


        if (title.isEmpty() || publisher.isEmpty() || genre.isEmpty() || isbn.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String insertBookQuery = """
        INSERT INTO books (title, publisher, genre, copies_left, isbn, created_at)
        VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    """;

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(insertBookQuery)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, publisher);
            preparedStatement.setString(3, genre);
            preparedStatement.setInt(4, copiesLeft);
            preparedStatement.setString(5, isbn);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Book added successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");

                titleField.clear();
                publisherField.clear();
                genreField.clear();
                copiesLeftField.clear();
                isbnField.clear();
                initialize();
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

    @FXML
    private TableView<Reservation> reviewReservationsTable;
    @FXML
    private TableColumn<Reservation, Integer> reservationIdColumn;
    @FXML
    private TableColumn<Reservation, String> patronNameColumn, bookTitleColumn, reservationDateColumn, hasBorrowedColumn;
    @FXML
    private TableColumn<Reservation, Integer> approveColumn;

    @FXML
    private TableColumn<Reservation, Void> actionColumn;

    private void loadPendingReservations() {
        String fetchReservationsQuery = """
        SELECT r.id AS reservation_id, p.name AS patron_name, b.title AS book_title,
               r.reservation_date AS reservation_date, r.has_borrowed AS has_borrowed
        FROM reservations r
        JOIN patrons p ON r.patron_id = p.id
        JOIN books b ON r.book_id = b.id
        WHERE r.has_borrowed = false
    """;

        ObservableList<Reservation> reservations = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(fetchReservationsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("reservation_id");
                String patronName = resultSet.getString("patron_name");
                System.out.println("Patron name for reservation ID: "+ id + " = " + patronName);
                String bookTitle = resultSet.getString("book_title");
                String reservationDate = resultSet.getString("reservation_date");
                boolean hasBorrowed = resultSet.getBoolean("has_borrowed");

                reservations.add(new Reservation(id, patronName, bookTitle, reservationDate, hasBorrowed));
            }

            reviewReservationsTable.setItems(reservations);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleApproval(int reservationId) {
        String updateReservationQuery = """
            UPDATE reservations
            SET has_borrowed = true
            WHERE id = ?
        """;

        String getReserverationQuery = """
                SELECT patron_id, book_id from reservations
                """;

        String addToBorrowingQuery = """
        INSERT INTO borrowing (patron_id, book_id, due_date)
        VALUES (?, ?, CURRENT_TIMESTAMP + INTERVAL 14 DAY)
    """;

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement updateStatement = connection.prepareStatement(updateReservationQuery);
             Statement getRservationStatement = connection.createStatement();
             PreparedStatement insertStatement = connection.prepareStatement(addToBorrowingQuery)) {

            // Update reservation status
            updateStatement.setInt(1, reservationId);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                int patronId = 0, bookId = 0;
                ResultSet resultSet = getRservationStatement.executeQuery(getReserverationQuery);
                while (resultSet.next()) {
                    patronId = resultSet.getInt("patron_id");
                    bookId = resultSet.getInt("book_id");

                }
                insertStatement.setInt(1, patronId);
                insertStatement.setInt(2, bookId);
                int borrowRowsAffected = insertStatement.executeUpdate();

                if (borrowRowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation approved and borrowing created successfully!");
                    initialize();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create borrowing record.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve reservation.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to approve reservation and create borrowing: " + e.getMessage());
            e.printStackTrace();
        }

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement statement = connection.prepareStatement(updateReservationQuery)){

            statement.setInt(1, reservationId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation approved successfully!");
                initialize();
                //loadReservations();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve reservation.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to approve reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleApproval(int reservationId, int patronId, int bookId) {
        String updateReservationQuery = """
        UPDATE reservations
        SET has_borrowed = true, status = 'Approved'
        WHERE id = ?
    """;

        String addToBorrowingQuery = """
        INSERT INTO borrowing (patron_id, book_id, due_date)
        VALUES (?, ?, CURRENT_TIMESTAMP + INTERVAL 14 DAY)
    """;


    }


    private void handleDecline(Reservation reservation) {
        String updateReservationQuery = "UPDATE reservations SET status = false WHERE id = ?";

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(updateReservationQuery)) {

            preparedStatement.setInt(1, reservation.getId());
            preparedStatement.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation declined successfully!");
            loadPendingReservations(); // Refresh table

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to decline reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
