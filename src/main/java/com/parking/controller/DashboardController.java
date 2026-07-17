package com.parking.controller;

import com.parking.database.DBConnection;
import com.parking.service.ParkingService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalVehiclesLabel;

    @FXML
    private Label studentsInsideLabel;

    @FXML
    private Label todaysEntriesLabel;

    private final ParkingService parkingService = new ParkingService();

    @FXML
    public void initialize() {

        if (Session.getCurrentAdmin() != null) {
            welcomeLabel.setText("Welcome, " +
                    Session.getCurrentAdmin().getUsername() + "!");
        }

        refreshStats();

    }

    private void refreshStats() {

        totalStudentsLabel.setText(String.valueOf(countRows("student")));

        totalVehiclesLabel.setText(String.valueOf(countRows("vehicle")));

        studentsInsideLabel.setText(
                String.valueOf(parkingService.countStudentsInside()));

        todaysEntriesLabel.setText(
                String.valueOf(parkingService.countTodaysEntries()));

    }

    private int countRows(String table) {

        String sql = "SELECT COUNT(*) FROM " + table;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {

                return rs.getInt(1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;
    }

}