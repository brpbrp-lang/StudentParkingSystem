package com.parking.util;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The Scanner Kiosk in this project is a USB / hardware QR scanner, which behaves like
 * a keyboard and simply "types" the QR content into a focused text field followed by Enter
 * (handled directly in ScannerController).
 *
 * This class provides an additional, optional decode path: reading a QR code from an
 * image file (e.g. if the kiosk uses a camera snapshot instead of a laser/keyboard scanner).
 */
public class QRScanner {

    /**
     * Decodes the QR code text from an image file.
     * @param imageFile the QR code image to decode
     * @return the decoded text, or null if no QR code could be found
     */
    public static String decodeQRFromImage(File imageFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            if (bufferedImage == null) {
                return null;
            }
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (IOException | NotFoundException e) {
            return null;
        }
    }
    public static String decodeQR(BufferedImage bufferedImage) {

        try {

            if (bufferedImage == null) {
                return null;
            }

            LuminanceSource source =
                    new BufferedImageLuminanceSource(bufferedImage);

            BinaryBitmap bitmap =
                    new BinaryBitmap(new HybridBinarizer(source));

            Result result =
                    new MultiFormatReader().decode(bitmap);

            return result.getText();

        } catch (Exception e) {
            return null;
        }

    }
}
