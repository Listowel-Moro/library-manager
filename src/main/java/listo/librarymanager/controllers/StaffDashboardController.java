package listo.librarymanager.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.*;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;
import lombok.Generated;

import java.sql.*;


public class StaffDashboardController {

    @FXML
    TableView<Book> searchResultsTable;
    @FXML private TextField searchField;
    @FXML private TableColumn<Book, String> titleColumn, publisherColumn, genreColumn, isbnColumn, copiesLeftColumn;

    final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML private Label accountName, accountPhone;

    private Staff currentUser;

    @Generated
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
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        approveColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));


        approveColumn.setCellFactory(column -> new TableCell<Reservation, Reservation>() {
            private final Hyperlink link = new Hyperlink("Approve");

            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null || reservation.getStatus().equals("QUEUED")) {
                    setGraphic(null);
                    setText(null);
                } else {
                    link.setOnAction(event -> {
                        handleApproval(reservation.getId());
                    });
                    setGraphic(link);
                    setText(null);
                }
            }
        });


        borrowingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        borrowedBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowedPatronNameColumn.setCellValueFactory(new PropertyValueFactory<>("patronName"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        checkInColumn.setCellFactory(column -> new TableCell<Borrowing, Integer>() {
            private final Hyperlink link = new Hyperlink("Check in");

            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    link.setOnAction(event -> {
                        handleCheckIn(id);
                    });
                    setGraphic(link);
                    setText(null);
                }
            }
        });


        loadBorrowedBooks();
        loadBooksFromDatabase();
        loadPendingReservations();
    }

    void loadBooksFromDatabase() {
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
            searchResultsTable.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearchBooksClick() {
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
    TextField titleField;
    @FXML TextField publisherField;
    @FXML TextField genreField;
    @FXML TextField copiesLeftField;
    @FXML TextField isbnField;
    @FXML Label messageLabel;

    @FXML
    public void onAddBookClick() {
        String title = titleField.getText();
        String publisher = publisherField.getText();
        String genre = genreField.getText();
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
            showAlert(Alert.AlertType.WARNING, "Invalid Copies left", "Defaulting to 1 because copies left is invalid");
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

    @FXML private TableView<Reservation> reviewReservationsTable;
    @FXML private TableColumn<Reservation, Integer> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> patronNameColumn, bookTitleColumn, reservationDateColumn, statusColumn;
    @FXML private TableColumn<Reservation, Reservation> approveColumn;

    private void loadPendingReservations() {
        String fetchReservationsQuery = """
        SELECT r.id AS reservation_id, r.status as reservation_status, p.name AS patron_name, b.title AS book_title,
               r.reservation_date AS reservation_date, r.has_borrowed AS has_borrowed
        FROM reservations r
        JOIN patrons p ON r.patron_id = p.id
        JOIN books b ON r.book_id = b.id
        WHERE r.status = 'PENDING' OR r.status = 'QUEUED'
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
                String status = resultSet.getString("reservation_status");

                reservations.add(new Reservation(id, patronName, bookTitle, reservationDate, status));
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
            SET status = 'APPROVED'
            WHERE id = ?
        """;

        String getReservationQuery = """
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
                ResultSet resultSet = getRservationStatement.executeQuery(getReservationQuery);
                while (resultSet.next()) {
                    patronId = resultSet.getInt("patron_id");
                    bookId = resultSet.getInt("book_id");

                }
                insertStatement.setInt(1, patronId);
                insertStatement.setInt(2, bookId);
                int borrowRowsAffected = insertStatement.executeUpdate();

                if (borrowRowsAffected > 0) {
                    initialize();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation approved and borrowing created successfully!");
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
                //showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation approved successfully!");
                initialize();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve reservation.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to approve reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private TableView<Borrowing> borrowedBooksTable;
    @FXML private TableColumn<Borrowing, Integer> borrowingIdColumn;
    @FXML private TableColumn<Borrowing, Integer> checkInColumn;
    @FXML private TableColumn<Borrowing, String> dueDateColumn, borrowedDateColumn, borrowedPatronNameColumn, borrowedBookTitleColumn;
    @FXML
    private final  ObservableList<Borrowing> borrowedBooks = FXCollections.observableArrayList();
    private void loadBorrowedBooks() {
        String query = """
        SELECT b.id AS borrowing_id, bk.title AS book_title,
               p.name AS patron_name, b.borrowed_date, b.due_date
        FROM borrowing b
        JOIN books bk ON b.book_id = bk.id
        JOIN patrons p ON b.patron_id = p.id
        WHERE b.is_returned = false
    """;
        borrowedBooks.clear();

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("borrowing_id");
                String bookTitle = resultSet.getString("book_title");
                String patronName = resultSet.getString("patron_name");
                String borrowedDate = resultSet.getString("borrowed_date");
                String dueDate = resultSet.getString("due_date");

                borrowedBooks.add(new Borrowing(id, patronName, bookTitle, borrowedDate, dueDate));
            }

            borrowedBooksTable.setItems(borrowedBooks);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load borrowed books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCheckIn(int id) {
        String updateBorrowingQuery = "UPDATE borrowing SET is_returned = true WHERE id = ?";
        String incrementCopiesQuery = """
        UPDATE books
        SET copies_left = copies_left + 1
        WHERE id = (SELECT book_id FROM borrowing WHERE id = ?)
    """;
        String fetchBookIdQuery = "SELECT book_id FROM borrowing WHERE id = ?";
        String fetchQueuedReservationQuery = """
        SELECT r.id
        FROM book_queue q
        JOIN reservations r ON q.reservation_id = r.id
        WHERE r.book_id = ? AND r.status = 'QUEUED'
        ORDER BY q.id ASC
        LIMIT 1
    """;
        String updateReservationStatusQuery = "UPDATE reservations SET status = 'PENDING' WHERE id = ?";
        String removeFromQueueQuery = "DELETE FROM book_queue WHERE reservation_id = ?";

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement updateBorrowingStmt = connection.prepareStatement(updateBorrowingQuery);
             PreparedStatement incrementCopiesStmt = connection.prepareStatement(incrementCopiesQuery);
             PreparedStatement fetchBookIdStmt = connection.prepareStatement(fetchBookIdQuery);
             PreparedStatement fetchQueuedReservationStmt = connection.prepareStatement(fetchQueuedReservationQuery);
             PreparedStatement updateReservationStatusStmt = connection.prepareStatement(updateReservationStatusQuery);
             PreparedStatement removeFromQueueStmt = connection.prepareStatement(removeFromQueueQuery)) {

            connection.setAutoCommit(false);

            // Update borrowing record to set is_returned = true
            updateBorrowingStmt.setInt(1, id);
            int rowsAffected = updateBorrowingStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Fetch the book ID associated with this borrowing
                fetchBookIdStmt.setInt(1, id);
                int bookId;
                try (ResultSet resultSet = fetchBookIdStmt.executeQuery()) {
                    if (resultSet.next()) {
                        bookId = resultSet.getInt("book_id");
                    } else {
                        connection.rollback();
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not find the associated book for this borrowing.");
                        return;
                    }
                }

                // Increment the book's copies_left
                incrementCopiesStmt.setInt(1, id);
                incrementCopiesStmt.executeUpdate();

                // Check if there is a queued reservation for this book
                fetchQueuedReservationStmt.setInt(1, bookId);
                try (ResultSet resultSet = fetchQueuedReservationStmt.executeQuery()) {
                    if (resultSet.next()) {
                        int reservationId = resultSet.getInt("id");

                        // Update reservation status to 'PENDING'
                        updateReservationStatusStmt.setInt(1, reservationId);
                        updateReservationStatusStmt.executeUpdate();

                        // Remove reservation from the queue
                        removeFromQueueStmt.setInt(1, reservationId);
                        removeFromQueueStmt.executeUpdate();

                        System.out.println("Reservation ID " + reservationId + " has been moved to 'PENDING'.");
                    }
                }

                connection.commit();
                System.out.println("Book with borrowing id: " + id + " has been checked in successfully.");
                showAlert(Alert.AlertType.INFORMATION, "Check-In Successful", "The book has been returned successfully.");
                initialize(); // Reload UI to reflect changes
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Check-In Failed", "No borrowing record found with the provided ID.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to check in the book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private TextField searchBorrowedBooksField;
    @FXML
    private void onSearchBorrowedBooksClick() {
        String searchQuery = searchBorrowedBooksField.getText().trim().toLowerCase();

        if (searchQuery.isEmpty()) {
            borrowedBooksTable.setItems(borrowedBooks);
            return;
        }

        ObservableList<Borrowing> filteredList = FXCollections.observableArrayList();
        for (Borrowing borrowing : borrowedBooks) {
            if (borrowing.getBookTitle().toLowerCase().contains(searchQuery) ||
                    borrowing.getPatronName().toLowerCase().contains(searchQuery) ||
                    borrowing.getBorrowedDate().toLowerCase().contains(searchQuery) ||
                    borrowing.getDueDate().toLowerCase().contains(searchQuery)) {
                filteredList.add(borrowing);
            }
        }
        borrowedBooksTable.setItems(filteredList);
    }

    void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
