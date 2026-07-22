package com.parking.controller;

import java.io.IOException;

import com.parking.service.ParkingFacade;
import com.parking.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainLayoutController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private StackPane contentPane;

    private final ParkingFacade parkingFacade = new ParkingFacade();

    @FXML
    public void initialize() {
        if (!parkingFacade.isLoggedIn())  {
            returnToLogin();
            return;
        }

        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/view/DashboardView.fxml");
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

        boolean confirm = AlertHelper.showConfirmation(
                "Logout",
                "Are you sure you want to logout?");

        if (!confirm) {
            return;
        }

        parkingFacade.logout();
        returnToLogin();
    }

    private void returnToLogin() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setTitle("Student Vehicle Parking Management System");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not return to the Login screen.");
        }

    }

    private void loadView(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            contentPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}