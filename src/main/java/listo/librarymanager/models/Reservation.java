package listo.librarymanager.models;

public class Reservation {
    int book_id, patron_id;
    Boolean has_borrowed;

    public Reservation(int book_id, int patron_id){
        this.book_id = book_id;
        this.patron_id = patron_id;
        this.has_borrowed = false;
    }

    private void reserveBook(){
        // check if book is available
        // if book available, add a new reservation (with book_id)
        // else add reservation to queue
        //
    }

    private boolean isBookAvailable(int book_id){
        // make a request to database to see if book is available
        return true;
    }

}

