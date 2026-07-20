package com.parking.model;


import java.io.Serializable;

public abstract class User implements Serializable{

    private static final long serialVersionUID = 1L;
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
