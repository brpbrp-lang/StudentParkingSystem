package com.parking.controller;

import com.parking.model.Administrator;

/**
 * Tiny in-memory session holder so any controller can know who is logged in
 * without re-querying the database or passing objects through every FXMLLoader call.
 */
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
