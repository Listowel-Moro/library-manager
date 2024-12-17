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

    public Borrowing(int id, String patronName, String bookTitle, String borrowedDate, String dueDate) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.isReturned = false; // Default to false when the book is borrowed
    }

    public int getId() {
        return id;
    }

    public String getPatronName() {
        return this.patronName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public String getDueDate() {
        return dueDate;
    }
}
