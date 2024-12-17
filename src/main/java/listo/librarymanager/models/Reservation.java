package listo.librarymanager.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {
    int id;
    private String bookTitle;
    private String reservationDate;
    private String patronName;
    private String status;

    public Reservation(int id, String patronName, String bookTitle, String reservationDate, String status) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.reservationDate = reservationDate;
        this.status = status;
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

    public String getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
