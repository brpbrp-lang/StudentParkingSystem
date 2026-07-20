package com.parking.service;

import com.parking.model.Administrator;

import java.io.*;

/**
 * Manages the current administrator session using Java Serialization.
 */
public final class Session {

    private static Administrator currentAdmin;

    private static final String SESSION_FILE = "session.dat";

    private Session() {
    }

    /**
     * Returns the currently logged-in administrator.
     */
    public static Administrator getCurrentAdmin() {

        // Return from memory if already loaded
        if (currentAdmin != null) {
            return currentAdmin;
        }

        File file = new File(SESSION_FILE);

        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {

            currentAdmin = (Administrator) ois.readObject();
            return currentAdmin;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Saves the administrator into session.dat
     */
    public static void setCurrentAdmin(Administrator admin) {

        currentAdmin = admin;

        System.out.println("Saving session...");
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {

            oos.writeObject(admin);

            System.out.println("session.dat created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the current session.
     */
    public static void clear() {

        currentAdmin = null;

        File file = new File(SESSION_FILE);

        if (file.exists()) {
            System.out.println("Deleting session.dat...");
            System.out.println(file.getAbsolutePath());

            if (file.delete()) {
                System.out.println("Session deleted successfully.");
            } else {
                System.out.println("Failed to delete session.");
            }
        }
    }



    /**
     * Checks whether a session exists.
     */
    public static boolean isLoggedIn() {
        return getCurrentAdmin() != null;
    }
}