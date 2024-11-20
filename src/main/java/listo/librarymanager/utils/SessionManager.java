package listo.librarymanager.utils;

import listo.librarymanager.models.User;

public class SessionManager {

    private static User currentUser;

    /**
     * Sets the current user in the session.
     * This method is used to store the logged-in user's details.
     *
     * @param user the user object representing the currently logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves the current user from the session.
     * This method is used to access the details of the logged-in user.
     *
     * @return the user object representing the currently logged-in user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current user session.
     * This method logs the user out by removing their details from the session.
     */
    public static void clearSession() {
        currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
