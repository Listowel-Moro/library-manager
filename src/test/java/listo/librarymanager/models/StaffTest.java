package listo.librarymanager.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff(1, "Abanga", "0200000000", false);
    }

    @Test
    void testStaffConstructor() {
        assertNotNull(staff);
        assertEquals(1, staff.getId());
        assertEquals("Abanga", staff.getName());
        assertEquals("0200000000", staff.getPhone());
    }

    @Test
    void testGetName() {
        assertEquals("Abanga", staff.getName());
    }

    @Test
    void testGetId() {
        assertEquals(1, staff.getId());
    }

    @Test
    void testGetPhone() {
        assertEquals("0200000000", staff.getPhone());
    }
}

