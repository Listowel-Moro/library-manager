package listo.librarymanager.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {
    int id;
    private String bookTitle, reservationDate, patronName;
    boolean hasBorrowed;

    public Reservation(int id, String patronName, String bookTitle, String reservationDate, boolean hasBorrowed){
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.reservationDate = reservationDate;
        this.hasBorrowed = hasBorrowed;
    }

    public int getId() {
        return id;
    }

    public String getPatronName(){
        return this.patronName;
    }

    public boolean getHasBorrowed(){
        return this.hasBorrowed;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getReservationDate() {
       return  reservationDate;
    }

}

