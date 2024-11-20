package listo.librarymanager.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Borrowing {
    private int id;
    private boolean isReturned;
    private String bookTitle;
    private String borrowedDate;
    private String dueDate;
    private String patronName;

    /**
     * Constructor to initialize a Borrowing object with its details.
     *
     * @param id          Unique ID of the borrowing record
     * @param patronName  Name of the patron who borrowed the book
     * @param bookTitle   Title of the borrowed book
     * @param borrowedDate Date when the book was borrowed
     * @param dueDate     Date when the borrowed book is due for return
     */
    public Borrowing(int id, String patronName, String bookTitle, String borrowedDate, String dueDate) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.isReturned = false; // Default to false when the book is borrowed
    }

    /**
     * Retrieves the ID of the borrowing record.
     *
     * @return Borrowing record ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the patron who borrowed the book.
     *
     * @return Patron's name
     */
    public String getPatronName() {
        return this.patronName;
    }

    /**
     * Retrieves the title of the borrowed book.
     *
     * @return Book title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the date when the book was borrowed.
     *
     * @return Borrowed date
     */
    public String getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * Retrieves the due date for returning the borrowed book.
     *
     * @return Due date
     */
    public String getDueDate() {
        return dueDate;
    }
}
