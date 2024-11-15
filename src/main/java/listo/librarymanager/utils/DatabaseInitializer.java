package listo.librarymanager.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String CREATE_PATRONS_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS patrons (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            phone VARCHAR(255) UNIQUE NOT NULL,
            isStaff BOOLEAN DEFAULT FALSE,
            password VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;

    private static final String CREATE_STAFF_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS staff (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            isStaff BOOLEAN DEFAULT FALSE,
            phone VARCHAR(255) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;

    private static  final String CREATE_BOOk_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                publisher VARCHAR(255) NOT NULL,
                genre VARCHAR(255),
                status VARCHAR(50),
                isbn VARCHAR(50) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    public static boolean initializeDatabase(Connection connection){
        try{
            Statement statement = connection.createStatement();
            statement.execute(CREATE_PATRONS_TABLE_QUERY);
            statement.execute(CREATE_STAFF_TABLE_QUERY);
            statement.execute(CREATE_BOOk_TABLE_QUERY);

            return true;

        } catch (Exception e){
            System.out.println("Error while creating tables " + e);
            return false;
        }
    }
}
