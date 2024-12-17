package listo.librarymanager.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HashPasswordTest {
    @Test
    void hashPasswordShouldBeDifferentFromOriginal(){
        assertNotEquals("password", HashPassword.hashPassword("password"));
    }

    @Test
    void hashedPasswordShouldNotBeNull(){
        assertNotNull(HashPassword.hashPassword("password"));
    }

    @Test
    void samePasswordShouldProduceSameHashes(){
        String hashedPasswordOne = HashPassword.hashPassword("password");
        String hashedPasswordTwo = HashPassword.hashPassword("password");

        assertEquals(hashedPasswordOne, hashedPasswordTwo);
    }

    @Test
    void differentPasswordShouldProduceDifferentHashes(){
        String hashedPasswordOne = HashPassword.hashPassword("password");
        String hashedPasswordTwo = HashPassword.hashPassword("password1");

        assertNotEquals(hashedPasswordOne, hashedPasswordTwo);
    }


    @Test
    void testHashPasswordThrowsRuntimeException() {
        try (var mockStatic = mockStatic(MessageDigest.class)) {
            mockStatic.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("Algorithm not found"));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    HashPassword.hashPassword("testPassword")
            );

            assertTrue(exception.getMessage().contains("Error hashing password"));
        }
}

}
