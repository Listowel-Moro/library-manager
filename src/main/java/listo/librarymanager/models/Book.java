package listo.librarymanager.models;

import java.util.List;

public class Book {
    String title;
    String publisher;
    String genre;
    String status;
    String isbn;
    int copiesLeft;

    public Book(String title, String publisher, String genre, String isbn, int copiesLeft) {
        this.title = title;
        this.publisher = publisher;
        this.genre = genre;
        this.status = "Available";
        this.isbn = isbn;
        this.copiesLeft = copiesLeft;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public int getCopiesLeft() {
        return this.copiesLeft;
    }
}
