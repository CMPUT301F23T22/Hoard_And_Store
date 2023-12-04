package com.example.hoard;
/**
 * This class is used to manage the currently logged in user.
 */
public class UserManager {
    private static UserManager instance;
    private User loggedInUser;

    /**
     * Constructor for the UserManager class.
     */
    private UserManager() {
        // private constructor to prevent instantiation
    }
    /**
     * Gets the instance of the UserManager class.
     *
     * @return The instance of the UserManager class.
     */
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {

        loggedInUser = user;
    }
    /**
     * Gets the logged in user.
     *
     * @return The logged in user.
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}