package com.parking.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QRGenerator {

    private static final String QR_FOLDER = "src/main/resources/qrcodes/";
    private static final int SIZE = 800;

    public static String generateQRCode(String studentID) {
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
