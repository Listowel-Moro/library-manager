package listo.librarymanager.models;

import java.util.List;

public class Staff extends User{
    public Staff(String name, String phone, String password, boolean isStaff) {
        super(name, phone, password);
        this.isStaff = true;
    }

    @Override
    public User registerUser() {
        // Make call to database to save user instance to db
        // Redirect user to login page
        return null;
    }

    @Override
    public User loginUser() {
        // Make call to database to check for user credentials
        // Redirect user to staff interface
        return null;
    }

    public String getName(){
        return this.name;
    }

    public String getPhone(){
        return this.phone;
    }

    public List<Book> getBorrowedBooks() {
        return List.of(new Book("Borrowed Book 1", "Author 1", "commedy", "1234"), new Book("Borrowed Book 2", "Author 2", "commedy", "1234"));

    }
}

