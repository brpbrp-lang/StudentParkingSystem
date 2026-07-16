package com.parking.model;

/**
 * Abstract base class for any user of the system (currently only Administrator,
 * but kept abstract to allow future roles).
 */
public abstract class User {

    protected String userID;
    protected String username;
    protected String password;

    public User() {
    }

    public User(String userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public abstract boolean login();

    public abstract void logout();

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
