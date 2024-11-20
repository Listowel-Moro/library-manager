package listo.librarymanager.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {
    int id;
    private String bookTitle;
    private String reservationDate;
    private String patronName;
    private String status;

    /**
     * Constructor to initialize a Reservation object with all required details.
     *
     * @param id              Unique ID of the reservation
     * @param patronName      Name of the patron who made the reservation
     * @param bookTitle       Title of the reserved book
     * @param reservationDate Date when the reservation was made
     * @param status          Status of the reservation (e.g., PENDING, QUEUED, APPROVED)
     */
    public Reservation(int id, String patronName, String bookTitle, String reservationDate, String status) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.patronName = patronName;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    /**
     * Retrieves the unique ID of the reservation.
     *
     * @return Reservation ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the patron who made the reservation.
     *
     * @return Patron name
     */
    public String getPatronName() {
        return this.patronName;
    }

    /**
     * Retrieves the title of the reserved book.
     *
     * @return Book title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the date when the reservation was made.
     *
     * @return Reservation date in string format
     */
    public String getReservationDate() {
        return reservationDate;
    }

    /**
     * Retrieves the current status of the reservation.
     *
     * @return Reservation status (e.g., PENDING, QUEUED, APPROVED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Updates the current status of the reservation.
     *
     * @param status New status to be set for the reservation
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
