package listo.librarymanager.utils;
import listo.librarymanager.models.Patron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SessionManagerTest {
    @Test
    void noLoggedInUserShouldReturnNull(){
        SessionManager.clearSession();
        assertNull(SessionManager.getCurrentUser());
    }

    @Test
    void loggedInUserShouldReturnCorrectUserName(){
        var user = new Patron(56, "Listo", "0342334223", false);
        SessionManager.setCurrentUser(user);
        assertEquals("Listo", SessionManager.getCurrentUser().getName());
    }

    @Test
    void loggedInUserShouldReturnCorrectUserPhone(){
        var user = new Patron(56, "Listo", "0342334223", false);
        SessionManager.setCurrentUser(user);
        assertEquals("0342334223", SessionManager.getCurrentUser().getPhone());
    }

    @Test
    void shouldReturnNullAfterClearingSession(){
        var user = new Patron(56, "Listo", "0342334223", false);
        SessionManager.setCurrentUser(user);
        SessionManager.clearSession();
        assertNull(SessionManager.getCurrentUser());
    }
}
