package listo.librarymanager.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BorrowingTest {

    private Borrowing borrowing;

    @BeforeEach
    void setUp() {
        borrowing = new Borrowing(1, "John Mensa", "Java for Beginners", "2024-12-01", "2024-12-14");
    }

    @Test
    void testConstructor() {
        assertNotNull(borrowing);
        assertEquals(1, borrowing.getId());
        assertEquals("John Mensa", borrowing.getPatronName());
        assertEquals("Java for Beginners", borrowing.getBookTitle());
        assertEquals("2024-12-01", borrowing.getBorrowedDate());
        assertEquals("2024-12-14", borrowing.getDueDate());
    }

    @Test
    void testGetId() {
        assertEquals(1, borrowing.getId());
    }

    @Test
    void testGetPatronName() {
        assertEquals("John Mensa", borrowing.getPatronName());
    }

    @Test
    void testGetBookTitle() {
        assertEquals("Java for Beginners", borrowing.getBookTitle());
    }

    @Test
    void testGetBorrowedDate() {
        assertEquals("2024-12-01", borrowing.getBorrowedDate());
    }

    @Test
    void testGetDueDate() {
        assertEquals("2024-12-14", borrowing.getDueDate());
    }
}
