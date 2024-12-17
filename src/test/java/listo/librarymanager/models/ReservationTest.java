package listo.librarymanager.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(1, "John Mensa", "Java Programming", "2024-12-10", "Pending");
    }

    @Test
    void testConstructor() {
        assertNotNull(reservation);
        assertEquals(1, reservation.getId());
        assertEquals("John Mensa", reservation.getPatronName());
        assertEquals("Java Programming", reservation.getBookTitle());
        assertEquals("2024-12-10", reservation.getReservationDate());
        assertEquals("Pending", reservation.getStatus());
    }

    @Test
    void testGetId() {
        assertEquals(1, reservation.getId());
    }

    @Test
    void testGetPatronName() {
        assertEquals("John Mensa", reservation.getPatronName());
    }

    @Test
    void testGetBookTitle() {
        assertEquals("Java Programming", reservation.getBookTitle());
    }

    @Test
    void testGetReservationDate() {
        assertEquals("2024-12-10", reservation.getReservationDate());
    }

    @Test
    void testGetStatus() {
        assertEquals("Pending", reservation.getStatus());
    }

    @Test
    void testSetStatus() {
        reservation.setStatus("Completed");
        assertEquals("Completed", reservation.getStatus());
    }
}
