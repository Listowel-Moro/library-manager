package listo.librarymanager.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Java Programming", "Abanga", "Education", "123456789", 5);
    }

    @Test
    void testConstructor() {
        assertNotNull(book);
        assertEquals("Java Programming", book.getTitle());
        assertEquals("Abanga", book.getPublisher());
        assertEquals("Education", book.getGenre());
        assertEquals("123456789", book.getIsbn());
        assertEquals(5, book.getCopiesLeft());
    }

    @Test
    void testGetTitle() {
        assertEquals("Java Programming", book.getTitle());
    }

    @Test
    void testGetPublisher() {
        assertEquals("Abanga", book.getPublisher());
    }

    @Test
    void testGetGenre() {
        assertEquals("Education", book.getGenre());
    }

    @Test
    void testGetIsbn() {
        assertEquals("123456789", book.getIsbn());
    }

    @Test
    void testGetCopiesLeft() {
        assertEquals(5, book.getCopiesLeft());
    }
}
