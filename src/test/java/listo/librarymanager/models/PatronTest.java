package listo.librarymanager.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatronTest {

    private Patron patron;

    @BeforeEach
    void setUp() {
        patron = new Patron(1, "Abanga", "0200000000", false);
    }

    @Test
    void testPatronConstructor() {
        assertNotNull(patron);
        assertEquals(1, patron.getId());
        assertEquals("Abanga", patron.getName());
        assertEquals("0200000000", patron.getPhone());
    }

    @Test
    void testGetName() {
        assertEquals("Abanga", patron.getName());
    }

    @Test
    void testGetId() {
        assertEquals(1, patron.getId());
    }

    @Test
    void testGetPhone() {
        assertEquals("0200000000", patron.getPhone());
    }
}
