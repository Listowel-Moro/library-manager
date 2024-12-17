package listo.librarymanager.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL="jdbc:mysql://127.0.0.1:3306/library_management_system";
    private static final String DB_USERNAME="root";
    private static final String DB_PASSWORD="Listo$tar01";

    private static String environment = "production";

public static Connection connectDatabase() throws SQLException {
        if (environment.equals("test")){
            return connectToTestDatabase();
        } else {
            return connectToProductionDatabase();
        }
    }

    public static void closeDatabaseConnection(Connection connection) throws SQLException {
        connection.close();
    }

    private static Connection connectToProductionDatabase() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    private static Connection connectToTestDatabase() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }

    public static void setEnvironment(String environ){
        environment = environ;
    }
}
