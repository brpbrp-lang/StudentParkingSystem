package com.parking.model;

/**
 * Represents the Administrator role. Actual CRUD / report operations are delegated
 * to the controller + service layer; this class mainly models session identity.
 */
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
