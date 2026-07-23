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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CameraService {

    private Webcam webcam;
    private ExecutorService executor;

    private volatile boolean running = false;


    private volatile String lockedQrCode;
    private volatile long lastLockedQrSeenAt;


    private volatile long lastDecodeAt;
    private static final long DECODE_INTERVAL_MS = 150;


    private static final long QR_REMOVAL_DELAY_MS = 2000;


    private volatile long lastPreviewAt;
    private static final long PREVIEW_INTERVAL_MS = 66;


    private final AtomicBoolean previewUpdatePending =
            new AtomicBoolean(false);

    public synchronized boolean start(
            ImageView imageView,
            Consumer<String> onQRCodeDetected) {


        if (running) {
            return true;
        }

        try {
            webcam = selectWebcam();

            if (webcam == null) {
                System.out.println("No webcam was detected.");
                return false;
            }

            System.out.println("Using camera: " + webcam.getName());


            setPreferredResolution(webcam);

            if (!webcam.isOpen()) {
                webcam.open();
            }

            if (!webcam.isOpen()) {
                System.out.println("The webcam could not be opened.");
                return false;
            }

            resetScannerState();

            running = true;

            executor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(
                        runnable,
                        "qr-camera-scanner"
                );

                thread.setDaemon(true);
                return thread;
            });

            executor.execute(() ->
                    runCameraLoop(imageView, onQRCodeDetected));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            stop();
            return false;
        }
    }


    private Webcam selectWebcam() {

        for (Webcam camera : Webcam.getWebcams()) {

            System.out.println(
                    "Found camera: " + camera.getName()
            );

            String cameraName =
                    camera.getName().toLowerCase();

            if (cameraName.contains("front")) {
                return camera;
            }
        }

        return Webcam.getDefault();
    }


    private void setPreferredResolution(Webcam selectedWebcam) {

        Dimension preferred =
                new Dimension(640, 480);

        for (Dimension size : selectedWebcam.getViewSizes()) {

            System.out.println(
                    "Supported resolution: "
                            + size.width + " x " + size.height
            );

            if (size.width == preferred.width
                    && size.height == preferred.height) {

                selectedWebcam.setViewSize(size);

                System.out.println(
                        "Selected resolution: 640 x 480"
                );

                return;
            }
        }

        System.out.println(
                "640 x 480 is not supported. Using the default resolution."
        );
    }

    private void runCameraLoop(
            ImageView imageView,
            Consumer<String> onQRCodeDetected) {

        System.out.println("Camera scanner thread started.");

        try {
            while (running
                    && webcam != null
                    && webcam.isOpen()) {

                BufferedImage frame;

                try {
                    frame = webcam.getImage();
                } catch (Exception e) {

                    if (running) {
                        e.printStackTrace();
                    }

                    break;
                }

                if (frame == null) {
                    sleepBriefly();
                    continue;
                }

                long currentTime =
                        System.currentTimeMillis();

                updateCameraPreview(
                        frame,
                        imageView,
                        currentTime
                );

                processQRFrame(
                        frame,
                        onQRCodeDetected,
                        currentTime
                );

                sleepBriefly();
            }

        } finally {
            running = false;
            System.out.println("Camera scanner thread stopped.");
        }
    }

    private void updateCameraPreview(
            BufferedImage frame,
            ImageView imageView,
            long currentTime) {

        if (currentTime - lastPreviewAt
                < PREVIEW_INTERVAL_MS) {

            return;
        }

        lastPreviewAt = currentTime;


        if (!previewUpdatePending.compareAndSet(
                false,
                true)) {

            return;
        }

        Image image =
                SwingFXUtils.toFXImage(frame, null);

        Platform.runLater(() -> {

            try {
                imageView.setImage(image);
            } finally {
                previewUpdatePending.set(false);
            }
        });
    }

    private void processQRFrame(
            BufferedImage frame,
            Consumer<String> onQRCodeDetected,
            long currentTime) {


        if (currentTime - lastDecodeAt
                < DECODE_INTERVAL_MS) {

            return;
        }

        lastDecodeAt = currentTime;

        String detectedQr =
                QRScanner.decodeQR(frame);

        if (detectedQr != null) {

            detectedQr = detectedQr.trim();

            if (detectedQr.isEmpty()) {
                detectedQr = null;
            }
        }


        if (lockedQrCode == null) {

            if (detectedQr != null) {

                lockedQrCode = detectedQr;
                lastLockedQrSeenAt = currentTime;

                String acceptedQr = detectedQr;

                System.out.println(
                        "QR accepted: " + acceptedQr
                );

                Platform.runLater(() ->
                        onQRCodeDetected.accept(acceptedQr));
            }

            return;
        }


        if (lockedQrCode.equals(detectedQr)) {

            lastLockedQrSeenAt = currentTime;
            return;
        }


        if (currentTime - lastLockedQrSeenAt
                >= QR_REMOVAL_DELAY_MS) {

            System.out.println(
                    "QR removed. Scanner unlocked."
            );

            lockedQrCode = null;
            lastLockedQrSeenAt = 0;
        }
    }

    private void sleepBriefly() {

        try {
            Thread.sleep(33);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void stop() {

        running = false;

        if (executor != null
                && !executor.isShutdown()) {

            executor.shutdownNow();
            executor = null;
        }

        if (webcam != null
                && webcam.isOpen()) {

            webcam.close();
        }

        webcam = null;

        resetScannerState();

        System.out.println("Camera service stopped.");
    }

    private void resetScannerState() {

        lockedQrCode = null;
        lastLockedQrSeenAt = 0;
        lastDecodeAt = 0;
        lastPreviewAt = 0;
        previewUpdatePending.set(false);
    }

    public BufferedImage getCurrentFrame() {

        if (webcam != null
                && webcam.isOpen()) {

            return webcam.getImage();
        }

        return null;
    }
}