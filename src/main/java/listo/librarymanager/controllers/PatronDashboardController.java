package listo.librarymanager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import listo.librarymanager.config.DatabaseConnection;
import listo.librarymanager.models.Book;
import listo.librarymanager.models.Borrowing;
import listo.librarymanager.models.Patron;
import listo.librarymanager.models.Reservation;
import listo.librarymanager.utils.NavigationManager;
import listo.librarymanager.utils.SessionManager;

//import javax.annotation.processing.Generated;
import lombok.Generated;

import java.sql.*;


public class PatronDashboardController {

    @FXML
    TableView<Book> searchResultsTable;
    @FXML private TextField searchField;
    @FXML private Label accountName;
    @FXML private Label accountPhone;

    private Patron currentUser;

    @FXML
    TableColumn<Book, String> titleColumn;
    @FXML
    TableColumn<Book, String> publisherColumn;
    @FXML
    TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, String> statusColumn;
    @FXML
    TableColumn<Book, String> isbnColumn;
    @FXML
    TableColumn<Book, String> reservationLinkColumn;
    final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @Generated
    public void initialize() {
        currentUser = (Patron) SessionManager.getCurrentUser();

        accountName.setText(currentUser.getName());
        accountPhone.setText(currentUser.getPhone());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        //statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        reservationLinkColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        reservationLinkColumn.setCellFactory(column -> new TableCell<Book, String>() {
            private final Hyperlink link = new Hyperlink();

            @Override
            protected void updateItem(String isbn, boolean empty) {
                super.updateItem(isbn, empty);
                if (empty || isbn == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    link.setText("Reserve book");
                    link.setOnAction(event -> {
                        // Handle the reservation logic here
                        System.out.println("Reserving book with ISBN: " + isbn);
                        reserveBook(isbn);
                    });
                    setGraphic(link);
                    setText(null);
                }
            }
        });

        // Load reservations table
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<Reservation, String>("reservationDate"));

        borrowingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        borrowedBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowedPatronNameColumn.setCellValueFactory(new PropertyValueFactory<>("patronName"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        loadBorrowedBooks();
        loadReservations();
        loadBooksFromDatabase();
    }

    void loadBooksFromDatabase() {
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

    void reserveBook(String isbn) {
        //System.out.println("COntroller user: " + SessionManager.getCurrentUser().getPhone());
        if (isbn == null || isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please provide a valid ISBN.");
            return;
        }

        if (SessionManager.getCurrentUser() == null) {
            showAlert(Alert.AlertType.ERROR, "User Not Logged In", "You need to log in to reserve a book.");
            return;
        }

        String fetchBookQuery = "SELECT id, title, copies_left FROM books WHERE isbn = ?";
        String createReservationQuery = "INSERT INTO reservations (book_id, patron_id, status) VALUES (?, ?, ?)";
        String addToQueueQuery = "INSERT INTO book_queue (reservation_id) VALUES (?)";

        int bookId = -1;
        int copiesLeft = 0;
        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement fetchBookStmt = connection.prepareStatement(fetchBookQuery)) {

            fetchBookStmt.setString(1, isbn);
            try (ResultSet resultSet = fetchBookStmt.executeQuery()) {
                if (resultSet.next()) {
                    bookId = resultSet.getInt("id");
                    copiesLeft = resultSet.getInt("copies_left");

                    if (copiesLeft <= 0) {
                        createReservationAndQueue(connection, bookId);
                        initialize();
                        showAlert(Alert.AlertType.WARNING, "No Copies Available", "This book is currently not available. You have been added to the queue.");
                        return;
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Book Not Found", "The book with the provided ISBN does not exist.");
                    return;
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
            e.printStackTrace();
            return;
        }

        // If the book is available, reserve the book (status = PENDING)
        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement reserveStmt = connection.prepareStatement(createReservationQuery)) {

            connection.setAutoCommit(false);

            // Create reservation with status = 'PENDING'
            reserveStmt.setInt(1, bookId);
            reserveStmt.setInt(2, SessionManager.getCurrentUser().getId());
            reserveStmt.setString(3, "PENDING");
            reserveStmt.executeUpdate();

            connection.commit();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book reserved successfully!");
            initialize();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while reserving the book.");
            e.printStackTrace();
        }
    }

    private void createReservationAndQueue(Connection connection, int bookId) throws SQLException {
        // Create reservation with status = 'QUEUED'
        String createReservationQuery = "INSERT INTO reservations (book_id, patron_id, status) VALUES (?, ?, ?)";
        String addToQueueQuery = "INSERT INTO book_queue (reservation_id) VALUES (?)";
        try (PreparedStatement reservationStmt = connection.prepareStatement(createReservationQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement queueStmt = connection.prepareStatement(addToQueueQuery)) {

            // Create reservation with status = 'QUEUED'
            reservationStmt.setInt(1, bookId);
            reservationStmt.setInt(2, SessionManager.getCurrentUser().getId());
            reservationStmt.setString(3, "QUEUED");
            reservationStmt.executeUpdate();

            // Get the generated reservation ID
            ResultSet generatedKeys = reservationStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int reservationId = generatedKeys.getInt(1);

                // Add the reservation to the queue
                queueStmt.setInt(1, reservationId);
                queueStmt.executeUpdate();
            }
        }
    }

    @FXML
    TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> reservationIdColumn, bookTitleColumn;
    @FXML private TableColumn<Reservation, String> reservationDateColumn;
    ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    void loadReservations() {
        String fetchReservationsQuery = """
        SELECT r.id AS reservation_id, r.status as reservation_status, b.title AS book_title, r.reservation_date as reservation_date
        FROM reservations r
        JOIN books b ON r.book_id = b.id
        WHERE r.patron_id = ? AND (r.status = 'PENDING' OR r.status = 'QUEUED')
    """;

        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(fetchReservationsQuery)) {

            preparedStatement.setInt(1, currentUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int reservationId = resultSet.getInt("reservation_id");
                    String bookTitle = resultSet.getString("book_title");
                    String reservationDate = resultSet.getString("reservation_date");
                    String status = resultSet.getString("reservation_status");

//                    LocalDate date = LocalDate.parse(reservationDate);
//                    String formattedReservationDate = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

                    reservations.add(new Reservation(reservationId, currentUser.getName(), bookTitle, reservationDate, status));
                }
            }

            reservationsTable.setItems(reservations);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML @Generated private TableView<Borrowing> borrowedBooksTable;
    @FXML private TableColumn<Borrowing, Integer> borrowingIdColumn;
    @FXML private TableColumn<Borrowing, String> borrowedBookTitleColumn, borrowedPatronNameColumn, borrowedDateColumn, dueDateColumn;
    @FXML private final  ObservableList<Borrowing> borrowedBooks = FXCollections.observableArrayList();
    private void loadBorrowedBooks() {
        String query = """
        SELECT b.id AS borrowing_id, bk.title AS book_title,
               p.name AS patron_name, b.borrowed_date, b.due_date
        FROM borrowing b
        JOIN books bk ON b.book_id = bk.id
        JOIN patrons p ON b.patron_id = p.id
        WHERE p.id = ? AND b.is_returned = false
    """;

        if (currentUser == null || currentUser.getId() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid user session.");
            return;
        }

        borrowedBooks.clear();
        try (Connection connection = DatabaseConnection.connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("borrowing_id");
                    String bookTitle = resultSet.getString("book_title");
                    String patronName = resultSet.getString("patron_name");
                    String borrowedDate = resultSet.getString("borrowed_date");
                    String dueDate = resultSet.getString("due_date");

                    borrowedBooks.add(new Borrowing(id, patronName, bookTitle, borrowedDate, dueDate));
                }
            }

            borrowedBooksTable.setItems(borrowedBooks);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load borrowed books: " + e.getMessage());
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

    @FXML
    public void patronLogout(){
        SessionManager.clearSession();
        NavigationManager.navigateTo("/listo/librarymanager/login-navigator.fxml");
    }
}