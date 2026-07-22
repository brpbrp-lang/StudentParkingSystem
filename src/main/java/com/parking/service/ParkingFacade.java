package com.parking.service;

import com.parking.model.Administrator;
import com.parking.model.ParkingLog;
import com.parking.model.Student;
import com.parking.model.Vehicle;

public class ParkingFacade {

    private final AuthenticationService authenticationService;
    private final ParkingService parkingService;

    public ParkingFacade() {
        authenticationService = new AuthenticationService();
        parkingService = new ParkingService();
    }

    // ----------------------------
    // LOGIN
    // ----------------------------

    public Administrator login(String username, String password) {

        Administrator admin =
                authenticationService.authenticate(username, password);

        if (admin != null) {
            Session.setCurrentAdmin(admin);
        }

        return admin;
    }

    public void logout() {

        if (Session.getCurrentAdmin() != null) {
            Session.getCurrentAdmin().logout();
        }

        Session.clear();
    }

    public boolean isLoggedIn() {
        return Session.isLoggedIn();
    }

    // ----------------------------
    // QR VERIFICATION
    // ----------------------------

    public Student verifyStudent(String qrCode) {
        return authenticationService.verifyQR(qrCode);
    }

    public Vehicle verifyVehicle(String studentID) {
        return authenticationService.verifyVehicle(studentID);
    }

    public ParkingLog checkActiveEntry(String studentID) {
        return authenticationService.checkActiveEntry(studentID);
    }

    // ----------------------------
    // PARKING
    // ----------------------------

    public boolean recordEntry(String studentID, int vehicleID) {
        return parkingService.recordEntry(studentID, vehicleID);
    }

    public boolean recordExit(int logID) {
        return parkingService.recordExit(logID);
    }

}