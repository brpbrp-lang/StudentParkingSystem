package com.parking.controller;

import com.google.zxing.NotFoundException;
import com.parking.model.Gate;
import com.parking.model.ParkingLog;
import com.parking.model.Student;
import com.parking.model.Vehicle;

import com.parking.service.CameraService;
import com.parking.service.ParkingFacade;


import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;


//verifyQR -> verifyStudent -> verifyRegisteredVehicle -> checkActiveParkingLog
//      -> recordTimeIn/recordTimeOut -> openGate -> display result -> reset scanner
public class ScannerController {

    @FXML private TextField qrInputField;
    @FXML private Label statusLabel;
    @FXML private Label detailLabel;

    private final ParkingFacade parkingFacade = new ParkingFacade();
    private final CameraService cameraService = new CameraService();
    private final Gate gate = new Gate();

    private volatile boolean processingScan = false;

    @FXML
    public void initialize() {

        setStatus("Starting Camera...", "ready-status");
        detailLabel.setText("Initializing webcam...");

        Platform.runLater(() -> {

            boolean opened = cameraService.start(cameraView, this::processScan);

            if (opened) {

                setStatus("Ready to Scan", "success-status");
                detailLabel.setText("Point your Student QR Code toward the camera.");

            } else {

                setStatus("Camera Error", "error-status");
                detailLabel.setText("No webcam detected.");

            }

        });

        cameraView.sceneProperty().addListener((obs, oldScene, newScene) -> {

            if (newScene != null) {

                newScene.getWindow().setOnHidden(e -> cameraService.stop());

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



    private synchronized void processScan(String qrCode) {
        System.out.println(
                "processScan() called: " + qrCode +
                        " | Thread = " + Thread.currentThread().getName()
        );
        if (processingScan)
            return;

        processingScan = true;

        // Step 1: verify QR code
        Student student = parkingFacade.verifyStudent(qrCode);
        if (student == null) {
            setStatus("Invalid QR Code", "error-status");
            detailLabel.setText("No matching student record for this code.");
            scheduleReset();
            return;
        }

        // Step 2: verify registered vehicle
        Vehicle vehicle = parkingFacade.verifyVehicle(student.getStudentID());
        if (vehicle == null) {
            setStatus("Vehicle Not Registered", "error-status");
            detailLabel.setText(student.getName() + " has no registered vehicle.");
            scheduleReset();
            return;
        }

        // Step 3: check active parking log (already inside?)
        ParkingLog activeLog = parkingFacade.checkActiveEntry(student.getStudentID());

        boolean success;
        String action;
        if (activeLog != null) {
            success = parkingFacade.recordExit(activeLog.getLogID());
            action = "EXIT";
        } else {
            success = parkingFacade.recordEntry(student.getStudentID(), vehicle.getVehicleID());
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

        PauseTransition pause = new PauseTransition(Duration.seconds(5));

        pause.setOnFinished(e -> resetScanner());

        pause.play();
    }

    private void resetScanner() {

        gate.closeGate();

        qrInputField.clear();

        processingScan = false;

        setStatus("Ready to Scan", "ready-status");
        detailLabel.setText("Point your Student QR Code toward the camera.");

        qrInputField.requestFocus();
    }

    private void setStatus(String text, String styleClass) {
        statusLabel.getStyleClass().removeAll("ready-status", "error-status", "success-status");
        statusLabel.getStyleClass().add(styleClass);
        statusLabel.setText(text);
    }


}
