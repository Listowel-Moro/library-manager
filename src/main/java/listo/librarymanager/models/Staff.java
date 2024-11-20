package listo.librarymanager.models;

import java.util.List;

public class Staff extends User {

    /**
     * Constructor to initialize a Staff object with user details.
     * The Staff member is always assigned `isStaff` as true, indicating they are a staff member.
     *
     * @param id    Unique ID of the staff
     * @param name  Name of the staff member
     * @param phone Phone number of the staff member
     * @param isStaff Boolean indicating if the user is staff (always true for Staff)
     */
    public Staff(int id, String name, String phone, boolean isStaff) {
        super(id, name, phone);
        this.isStaff = true;
    }

    /**
     * Registers a staff user in the system by saving their details to the database.
     * This method also handles redirecting the user to the login page.
     *
     * @return The registered User object (null for now, as database interaction is not implemented)
     */
    @Override
    public User registerUser() {
        // Make call to database to save user instance to db
        // Redirect user to login page
        return null;
    }

    /**
     * Logs in a staff user by validating credentials and redirecting to the staff interface.
     * This method would involve checking the user's credentials in the database.
     *
     * @return The logged-in User object (null for now, as database interaction is not implemented)
     */
    @Override
    public User loginUser() {
        // Make call to database to check for user credentials
        // Redirect user to staff interface
        return null;
    }

    /**
     * Retrieves the name of the staff member.
     *
     * @return Staff member's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the phone number of the staff member.
     *
     * @return Staff member's phone number
     */
    public String getPhone() {
        return this.phone;
    }
}
