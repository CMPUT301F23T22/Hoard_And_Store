package com.example.hoard;

public class UserManager {
    private static UserManager instance;
    private User loggedInUser;

    private UserManager() {
        // private constructor to prevent instantiation
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {

        loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}