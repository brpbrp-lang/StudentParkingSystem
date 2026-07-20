package com.parking.service;

import com.github.sarxos.webcam.Webcam;
import com.parking.util.QRScanner;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CameraService {

    private Webcam webcam;
    private ExecutorService executor;
    private volatile boolean running = false;
    private volatile boolean scanning = true;

    public boolean start(ImageView imageView, Consumer<String> onQRCodeDetected) {

        for (Webcam cam : Webcam.getWebcams()) {

            System.out.println("Found camera: " + cam.getName());

            if (cam.getName().contains("Rear")) {
                webcam = cam;
                break;
            }

        }

        if (webcam == null) {
            webcam = Webcam.getDefault();
        }

        System.out.println("Using camera: " + webcam.getName());

        for (Dimension d : webcam.getViewSizes()) {
            System.out.println(d.width + " x " + d.height);
        }
        if (!webcam.isOpen()){
            webcam.open();
            System.out.println("Camera is opened");
        }

        running = true;

        executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            while (running) {

               // System.out.println("Loop running");

                BufferedImage frame = webcam.getImage();

                // Camera may not be ready yet
                if (frame == null) {
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                // Save frame for debugging
                try {
                    javax.imageio.ImageIO.write(frame, "png",
                            new java.io.File("lastFrame.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Show camera preview
                Image image = SwingFXUtils.toFXImage(frame, null);

                Platform.runLater(() ->
                        imageView.setImage(image));

                // Detect QR

                if (scanning) {
                    String qrText = QRScanner.decodeQR(frame);

                    if (qrText != null) {

                        scanning = false;
                        System.out.println("QR FOUND: " + qrText);

                        Platform.runLater(() ->
                                onQRCodeDetected.accept(qrText));

                    }
                }
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        });

        return true;
    }

    public void stop() {

        running = false;

        if (executor != null) {
            executor.shutdownNow();
        }

        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }

    }

    public BufferedImage getCurrentFrame() {

        if (webcam != null && webcam.isOpen()) {
            return webcam.getImage();
        }

        return null;
    }

    public void resumeScanning(){
        scanning = true;


    }

}