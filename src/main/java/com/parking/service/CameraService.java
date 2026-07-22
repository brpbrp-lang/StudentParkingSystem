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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CameraService {

    private Webcam webcam;
    private ExecutorService executor;
    private volatile boolean running = false;
    // Recently scanned QR codes
    private final Map<String, Long> recentScans = new ConcurrentHashMap<>();

    // Ignore duplicate scans for 5 seconds
    private static final long DUPLICATE_DELAY = 5000;

    private static int threadCounter = 0;
    public boolean start(ImageView imageView, Consumer<String> onQRCodeDetected) {

        for (Webcam cam : Webcam.getWebcams()) {

         //   System.out.println("Found camera: " + cam.getName());

            if (cam.getName().contains("Front")) {
                webcam = cam;
                break;
            }

        }

        if (webcam == null) {
            webcam = Webcam.getDefault();
        }

       // System.out.println("Using camera: " + webcam.getName());

        for (Dimension d : webcam.getViewSizes()) {
            System.out.println(d.width + " x " + d.height);
        }
        if (!webcam.isOpen()){
            webcam.open();
          //  System.out.println("Camera is opened");
        }

        running = true;

        threadCounter++;
        System.out.println("Starting scanner thread #" + threadCounter);
        executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            System.out.println("Scanner thread is running.");
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

                String qrText = QRScanner.decodeQR(frame);

                if (qrText != null) {

                    long now = System.currentTimeMillis();

                    Long lastTime = recentScans.get(qrText);

                    System.out.println("--------------------------------");
                    System.out.println("Detected QR : " + qrText);
                    System.out.println("Last Time   : " + lastTime);

                    if (lastTime != null) {
                        System.out.println("Elapsed(ms) : " + (now - lastTime));
                    }

                    // Ignore if this QR was scanned recently
                    if (lastTime != null && (now - lastTime) < DUPLICATE_DELAY) {

                        System.out.println(">>> DUPLICATE IGNORED <<<");

                        continue;
                    }

                    // Remember this QR
                    recentScans.put(qrText, now);

                    System.out.println(">>> ACCEPTED <<<");

                    Platform.runLater(() -> onQRCodeDetected.accept(qrText));
                }
                long currentTime = System.currentTimeMillis();

                recentScans.entrySet().removeIf(entry ->
                        currentTime - entry.getValue() > DUPLICATE_DELAY);
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Stopping scanner thread.");

        });

        return true;
    }

    public void stop() {

        running = false;
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }

    }

    public BufferedImage getCurrentFrame() {

        if (webcam != null && webcam.isOpen()) {
            return webcam.getImage();
        }

        return null;
    }


}