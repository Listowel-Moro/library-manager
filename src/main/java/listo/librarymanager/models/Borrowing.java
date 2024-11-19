package listo.librarymanager.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Borrowing {
    private int id;
    private boolean isReturned;
    private String bookTitle, borrowedDate, returnDate, patronName;

    public Borrowing(int id, String patronName, String bookTitle, String borrowedDate, String returnDate, boolean isReturned){
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }

    public int getId() {
        return id;
    }

    public String getPatronName(){
        return this.patronName;
    }

    public boolean isReturned(){
        return this.isReturned;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBorrowedDate() {
        return  borrowedDate;
    }

    public String getReturnDate() {
        return  returnDate;
    }

}


