package com.parking.model;

public class Administrator extends User {

    private boolean loggedIn = false;

    public Administrator() {
        super();
    }

    public Administrator(String userID, String username, String password) {
        super(userID, username, password);
    }

    @Override
    public boolean login() {
        // Real credential validation happens in AuthenticationService.
        // This flag simply tracks session state once validated.
        loggedIn = true;
        return loggedIn;
    }

    @Override
    public void logout() {
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
