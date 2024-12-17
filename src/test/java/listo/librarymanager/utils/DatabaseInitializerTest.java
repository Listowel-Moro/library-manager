package listo.librarymanager.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializerTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testInitializeDatabaseSuccessFully() {
        boolean result = DatabaseInitializer.initializeDatabase(connection);

        assertTrue(result, "The database should be initialized successfully");
        assertTableExists("patrons");
        assertTableExists("staff");
        assertTableExists("books");
        assertTableExists("reservations");
        assertTableExists("borrowing");
        assertTableExists("book_queue");


    }

    @Test
    void testInitializeDatabaseUnsuccessfully() throws SQLException {
        connection.close();

        boolean result = DatabaseInitializer.initializeDatabase(connection);

        assertFalse(result, "The database initialization should fail when the connection is closed");
    }

    @Test
    void testClassInitialization() {
        assertNotNull(DatabaseInitializer.class);
    }

    private void assertTableExists(String tableName) {
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableName.toUpperCase() + "'"
            );
            assertTrue(resultSet.next(), "Table " + tableName + " should exist");
        } catch (SQLException e) {
            fail("Failed to check if table " + tableName + " exists: " + e.getMessage());
        }
    }
}
