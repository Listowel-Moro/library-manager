package listo.librarymanager.models;

import java.util.List;

public class Patron extends User {
    /**
     * Constructor to initialize a Patron object with user details.
     * The Patron is always not a staff member, so the isStaff field is set to false.
     *
     * @param id    Unique ID of the patron
     * @param name  Name of the patron
     * @param phone Phone number of the patron
     * @param isStaff Boolean indicating if the user is staff (always false for Patron)
     */
    public Patron(int id, String name, String phone, boolean isStaff) {
        super(id, name, phone);
        this.isStaff = false;
    }

    /**
     * Registers a patron user in the system by saving their details to the database.
     * This method also handles redirecting the user to the login page.
     *
     * @return The registered User object (null for now, as database interaction is not implemented)
     */
    @Override
    public User registerUser() {
        // Make call to database to save user
        // Redirect user to login page
        return null;
    }

    /**
     * Retrieves the name of the patron.
     *
     * @return Patron's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the unique ID of the patron.
     *
     * @return Patron's ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Retrieves the phone number of the patron.
     *
     * @return Patron's phone number
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Logs in a patron user by validating credentials and redirecting to the patron interface.
     * This method would involve checking the user's credentials in the database.
     *
     * @return The logged-in User object (null for now, as database interaction is not implemented)
     */
    @Override
    public User loginUser() {
        // Make call to database to check for user credentials
        // Redirect user to patron interface
        return null;
    }
}
