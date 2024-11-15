package listo.librarymanager.models;

import java.util.List;

public class Book {
    String title, publisher, genre, status, isbn;

    public Book(String title, String publisher, String genre, String isbn){
        this.title = title;
        this.publisher = publisher;
        this.genre = genre;
        this.status = "Available";
        this.isbn = isbn;
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

    public String getStatus() {
        return this.status;
    }

    public static List<Book> searchBooks(String searchQuery) {
        return List.of(new Book("Book 1", "Author 1", "commedy", "1234"), new Book("Book 2", "Author 2", "commedy", "1234"));
    }

    private Book addBook(){
        // Make call to database to save book instance
        System.out.println("Book successfully Added");
        return null;
    }

    private Book removeBook(){
        // Make call to database to delete book from database
        System.out.println("Book successfully removed");
        return null;
    }

}