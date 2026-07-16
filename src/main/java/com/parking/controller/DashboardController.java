package com.parking.controller;

import com.parking.database.DBConnection;
import com.parking.service.ParkingService;
import com.parking.util.AlertHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

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
            welcomeLabel.setText("Welcome, " + Session.getCurrentAdmin().getUsername() + "!");
        }
        refreshStats();
    }

    private void refreshStats() {
        totalStudentsLabel.setText(String.valueOf(countRows("student")));
        totalVehiclesLabel.setText(String.valueOf(countRows("vehicle")));
        studentsInsideLabel.setText(String.valueOf(parkingService.countStudentsInside()));
        todaysEntriesLabel.setText(String.valueOf(parkingService.countTodaysEntries()));
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

    @FXML
    private void showStudents() {
        loadView("/view/Student.fxml");
    }

    @FXML
    private void showVehicles() {
        loadView("/view/Vehicle.fxml");
    }

    @FXML
    private void showParkingLogs() {
        loadView("/view/ParkingLogs.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/view/Reports.fxml");
    }

    @FXML
    private void showDashboard() {
        refreshStats();
        loadView(null); // reload nothing, dashboard already showing; kept for the nav button
    }

    @FXML
    private void openScannerKiosk() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Scanner.fxml"));
            Parent root = loader.load();
            Stage kioskStage = new Stage();
            kioskStage.initModality(Modality.NONE);
            kioskStage.setTitle("Scanner Kiosk");
            Scene scene = new Scene(root, 700, 500);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            kioskStage.setScene(scene);
            kioskStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not open the Scanner Kiosk.");
        }
    }

    @FXML
    private void handleLogout() {
        boolean confirm = AlertHelper.showConfirmation("Logout", "Are you sure you want to logout?");
        if (!confirm) {
            return;
        }
        if (Session.getCurrentAdmin() != null) {
            Session.getCurrentAdmin().logout();
        }
        Session.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setTitle("Student Vehicle Parking Management System");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not return to the Login screen.");
        }
    }

    private void loadView(String fxmlPath) {
        if (fxmlPath == null) {
            return;
        }
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not load the requested screen.");
        }
    }
}
