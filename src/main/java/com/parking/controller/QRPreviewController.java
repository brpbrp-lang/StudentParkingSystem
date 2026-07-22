package com.parking.controller;

import com.parking.model.Student;
import com.parking.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;


import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class QRPreviewController {



    @FXML
    private VBox rootVBox;

    @FXML
    private VBox printArea;

    @FXML
    private ImageView qrImage;

    @FXML
    private Label studentLabel;

    private Student student;


    //Receives the student information
    public void setData(Student student) {

        this.student = student;

        studentLabel.setText(
                "Name: " + student.getName()
                        + "\nStudent ID: " + student.getStudentID()
                        + "\nCourse: " + student.getCourse()
                        + "\nYear Level: " + student.getYearLevel()
        );

        if (student.getQrCode() != null) {

            File file = new File(student.getQrCode());

            if (file.exists()) {
                qrImage.setImage(new Image(file.toURI().toString()));
            }

        }

    }

    //Prints the entire parking pass.
    @FXML
    private void printQR() {

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            System.out.println("No printer found.");
            return;
        }

        Stage stage = (Stage) rootVBox.getScene().getWindow();

        boolean proceed = job.showPrintDialog(stage);

        if (proceed) {

            // Shrink the printable area temporarily
            double scale = 0.85;

            printArea.setScaleX(scale);
            printArea.setScaleY(scale);

// Take a snapshot of the smaller layout
            SnapshotParameters params = new SnapshotParameters();
            WritableImage snapshot = printArea.snapshot(params, null);

// Restore the original size so the preview window is unchanged
            printArea.setScaleX(1);
            printArea.setScaleY(1);

// Create the printable image
            ImageView printView = new ImageView(snapshot);
            printView.setPreserveRatio(true);

// Print it
            boolean printed = job.printPage(printView);

            if (printed) {
                job.endJob();
                System.out.println("QR printed successfully.");
            }else {
                System.out.println("Printing failed.");
            }

        }

    }


    //Closes the preview window.
    @FXML
    private void close() {

        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handleSaveAs() {

        if (student == null || student.getQrCode() == null) {
            return;
        }

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Save QR Code");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PNG Image",
                        "*.png"
                )
        );

        chooser.setInitialFileName(student.getStudentID() + ".png");

        File destination =
                chooser.showSaveDialog(qrImage.getScene().getWindow());

        if (destination == null) {
            return;
        }

        try {

            Files.copy(
                    Paths.get(student.getQrCode()),
                    destination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            AlertHelper.showInfo(
                    "Success",
                    "QR Code saved successfully."
            );

        } catch (IOException e) {

            AlertHelper.showError(
                    "Error",
                    "Unable to save the QR Code."
            );

        }

    }

}