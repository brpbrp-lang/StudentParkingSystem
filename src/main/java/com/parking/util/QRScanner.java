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

public class QRScanner {

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
