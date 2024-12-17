package listo.librarymanager.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseConfigTest {

    private Connection mockConnection;
    private MockedStatic<DriverManager> mockedDriverManager;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);

        // Mock static methods of DriverManager using mockStatic
        mockedDriverManager = mockStatic(DriverManager.class);

        // Stub the getConnection method to return the mock connection
        mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                .thenReturn(mockConnection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Close the static mock
        System.out.println("Executed again");
        mockedDriverManager.close();

        // Clear caches and close the mock connection
        if (mockConnection != null && !mockConnection.isClosed()) {
            try {
                mockConnection.close();
            } catch (SQLException e) {
                // Handle potential exception (logging or suppressing if needed)
            }
        }
    }

    @Test
    public void testConnectDatabase() throws SQLException {
        // Call the method under test
        Connection connection = DatabaseConnection.connectDatabase();

        // Assert the connection is not null and is open
        assertNotNull(connection);
        assertFalse(connection.isClosed());

        //verify(mockedDriverManager, times(1)).getConnection(anyString(), anyString(), anyString());
    }

    @Test
    public void testCloseDatabaseConnection() throws SQLException {
        // Call the method to close the database connection
        DatabaseConnection.closeDatabaseConnection(mockConnection);

        // Verify that the close method was called on the mock connection
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testCloseDatabaseConnectionWithException() throws SQLException {
        // Make the close method throw an exception
        doThrow(new SQLException()).when(mockConnection).close();

        assertThrows(SQLException.class, () -> DatabaseConnection.closeDatabaseConnection(mockConnection));
    }

//    @Test
//    public void testConnectDatabaseWithException() throws SQLException {
//        mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
//                .thenThrow(new SQLException("Failed to connect database"));
//
//        assertThrows(SQLException.class, DatabaseConnection::connectDatabase);
//    }
}
