package listo.librarymanager.models;

import java.util.List;

public class Book {
    String title;
    String publisher;
    String genre;
    String status;
    String isbn;
    int copiesLeft;

    /**
     * Constructor to initialize a new Book object with its details.
     *
     * @param title      Title of the book
     * @param publisher  Publisher of the book
     * @param genre      Genre of the book
     * @param isbn       ISBN of the book (unique)
     * @param copiesLeft Number of copies left available in the library
     */
    public Book(String title, String publisher, String genre, String isbn, int copiesLeft) {
        this.title = title;
        this.publisher = publisher;
        this.genre = genre;
        this.status = "Available"; // Default status is "Available"
        this.isbn = isbn;
        this.copiesLeft = copiesLeft;
    }

    /**
     * Retrieves the title of the book.
     *
     * @return Book title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Retrieves the publisher of the book.
     *
     * @return Publisher name
     */
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * Retrieves the genre of the book.
     *
     * @return Book genre
     */
    public String getGenre() {
        return this.genre;
    }

    /**
     * Retrieves the ISBN of the book.
     *
     * @return Book ISBN
     */
    public String getIsbn() {
        return this.isbn;
    }

    /**
     * Retrieves the number of copies available for this book.
     *
     * @return Copies available
     */
    public int getCopiesLeft() {
        return this.copiesLeft;
    }
}
