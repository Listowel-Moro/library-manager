package listo.librarymanager.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // SQL query to create the "patrons" table
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

    // SQL query to create the "staff" table
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

    // SQL query to create the "books" table
    private static final String CREATE_BOOk_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                publisher VARCHAR(255) NOT NULL,
                genre VARCHAR(255),
                status VARCHAR(50),
                copies_left INT DEFAULT 1,
                isbn VARCHAR(50) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    // SQL query to create the "reservations" table
    private static final String CREATE_RESERVATION_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS reservations (
                id INT AUTO_INCREMENT PRIMARY KEY,
                patron_id INT NOT NULL,
                book_id INT NOT NULL,
                reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                has_borrowed BOOLEAN DEFAULT false,
                status ENUM('PENDING', 'QUEUED', 'APPROVED') DEFAULT 'PENDING',
                FOREIGN KEY (patron_id) REFERENCES patrons(id) ON DELETE CASCADE,
                FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
            );
            """;

    // SQL query to create the "borrowing" table
    private static final String CREATE_BORROWING_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS borrowing (
                id INT AUTO_INCREMENT PRIMARY KEY,
                patron_id INT NOT NULL,
                book_id INT NOT NULL,
                is_returned BOOLEAN DEFAULT false,
                borrowed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                due_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (patron_id) REFERENCES patrons(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            );
            """;

    // SQL query to create the "book_queue" table
    private static final String CREATE_BOOK_QUEUE_QUERY = """
            CREATE TABLE IF NOT EXISTS book_queue (
                id INT AUTO_INCREMENT PRIMARY KEY,
                reservation_id INT NOT NULL,
                request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
            );
    """;

    /**
     * Initializes the database by creating all necessary tables if they do not already exist.
     *
     * @param connection the database connection to use for table creation
     * @return true if all tables are successfully created; false otherwise
     */
    public static boolean initializeDatabase(Connection connection) {
        try {
            // Create a statement to execute SQL queries
            Statement statement = connection.createStatement();
            // Execute queries to create each table
            statement.execute(CREATE_PATRONS_TABLE_QUERY);
            statement.execute(CREATE_STAFF_TABLE_QUERY);
            statement.execute(CREATE_BOOk_TABLE_QUERY);
            statement.execute(CREATE_RESERVATION_TABLE_QUERY);
            statement.execute(CREATE_BORROWING_TABLE_QUERY);
            statement.execute(CREATE_BOOK_QUEUE_QUERY);

            return true;

        } catch (Exception e) {
            // Print error details to the console
            System.out.println("Error while creating tables " + e);
            return false;
        }
    }
}
