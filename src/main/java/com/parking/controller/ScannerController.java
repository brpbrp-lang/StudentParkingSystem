package com.parking.controller;

import com.google.zxing.NotFoundException;
import com.parking.model.Gate;
import com.parking.model.ParkingLog;
import com.parking.model.Student;
import com.parking.model.Vehicle;
import com.parking.service.AuthenticationService;
import com.parking.service.CameraService;
import com.parking.service.ParkingService;

import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * Drives the Scanner Kiosk screen.
 *
 * A hardware USB QR scanner behaves like a keyboard: it "types" the encoded text into
 * whatever field has focus, followed by an Enter keystroke. qrInputField below stays
 * focused at all times and its onAction (triggered by Enter) runs the verification chain:
 *
 *   verifyQR -> verifyStudent -> verifyRegisteredVehicle -> checkActiveParkingLog
 *     -> recordTimeIn/recordTimeOut -> openGate -> display result -> reset scanner
 */
public class ScannerController {

    @FXML private TextField qrInputField;
    @FXML private Label statusLabel;
    @FXML private Label detailLabel;

    private final AuthenticationService authService = new AuthenticationService();
    private final ParkingService parkingService = new ParkingService();
    private final CameraService cameraService = new CameraService();
    private final Gate gate = new Gate();



    @FXML
    public void initialize() {

        setStatus("Starting Camera...", "ready-status");
        detailLabel.setText("Initializing webcam...");

        Platform.runLater(() -> {

            boolean opened = cameraService.start(cameraView, qrCode -> {

                processScan(qrCode);

            });
            if (opened) {

                setStatus("Ready to Scan", "success-status");
                detailLabel.setText("Point your Student QR Code toward the camera.");

            } else {

                setStatus("Camera Error", "error-status");
                detailLabel.setText("No webcam detected.");

            }

        });

    }

    @FXML
    private void handleScan() {
        String qrCode = qrInputField.getText() == null ? "" : qrInputField.getText().trim();
        if (qrCode.isEmpty()) {
            resetScanner();
            return;
        }

        processScan(qrCode);
    }
    @FXML
    private ImageView cameraView;



    private void processScan(String qrCode) {
        // Step 1: verify QR code
        Student student = authService.verifyQR(qrCode);
        if (student == null) {
            setStatus("Invalid QR Code", "error-status");
            detailLabel.setText("No matching student record for this code.");
            scheduleReset();
            return;
        }

        // Step 2: verify registered vehicle
        Vehicle vehicle = authService.verifyVehicle(student.getStudentID());
        if (vehicle == null) {
            setStatus("Vehicle Not Registered", "error-status");
            detailLabel.setText(student.getName() + " has no registered vehicle.");
            scheduleReset();
            return;
        }

        // Step 3: check active parking log (already inside?)
        ParkingLog activeLog = authService.checkActiveEntry(student.getStudentID());

        boolean success;
        String action;
        if (activeLog != null) {
            success = parkingService.recordExit(activeLog.getLogID());
            action = "EXIT";
        } else {
            success = parkingService.recordEntry(student.getStudentID(), vehicle.getVehicleID());
            action = "ENTRY";
        }

        if (!success) {
            setStatus("System Error", "error-status");
            detailLabel.setText("Could not record " + action.toLowerCase() + ". Please try again.");
            scheduleReset();
            return;
        }

        // Step 4: open gate
        gate.openGate();

        // Step 5: display result
        setStatus("Access Granted", "success-status");
        detailLabel.setText(student.getName() + " (" + student.getStudentID() + ") - " +
                vehicle.getPlateNumber() + " - " + action + " recorded.");

        scheduleReset();
    }

    private void scheduleReset() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> resetScanner());
        pause.play();
    }

    private void resetScanner() {
        gate.closeGate();
        qrInputField.clear();
        setStatus("Ready to Scan", "ready-status");
        detailLabel.setText("Scan your Student QR Code to continue.");
        qrInputField.requestFocus();
    }

    private void setStatus(String text, String styleClass) {
        statusLabel.getStyleClass().removeAll("ready-status", "error-status", "success-status");
        statusLabel.getStyleClass().add(styleClass);
        statusLabel.setText(text);
    }
}
