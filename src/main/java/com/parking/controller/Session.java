package com.parking.controller;

import com.parking.model.Administrator;

//Manages the current user session for the application
//is like temporary ID holder for the administrator who is currently using the system.
public final class Session {

    private static Administrator currentAdmin;

    private Session() {
    }

    public static Administrator getCurrentAdmin() {
        return currentAdmin;
    }

    public static void setCurrentAdmin(Administrator admin) {
        currentAdmin = admin;
    }

    public static void clear() {
        currentAdmin = null;
    }
}
