package com.parking.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates QR code images for students and stores them under
 * src/main/resources/qrcodes/{studentID}.png
 */
public class QRGenerator {

    private static final String QR_FOLDER = "src/main/resources/qrcodes/";
    private static final int SIZE = 800;

    /**
     * Generates a QR code that encodes the student's ID and saves it as a PNG.
     * @param studentID the student ID to encode
     * @return the relative file path of the saved QR code image, or null on failure
     */
    public static String generateQRCode(String studentID) {
//        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        try {
            Path folder = Paths.get(QR_FOLDER);
            if (!java.nio.file.Files.exists(folder)) {
                java.nio.file.Files.createDirectories(folder);
            }

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(studentID, BarcodeFormat.QR_CODE, SIZE, SIZE);

            String fileName = QR_FOLDER + studentID + ".png";
            Path path = Paths.get(fileName);
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            return fileName;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
