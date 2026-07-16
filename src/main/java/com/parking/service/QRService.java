package com.parking.service;

import com.parking.database.DBConnection;
import com.parking.util.QRGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Generates a student's QR code and persists the resulting file path to the database.
 */
public class QRService {

    public String generateAndSaveQRCode(String studentID) {
        String path = QRGenerator.generateQRCode(studentID);
        if (path != null) {
            String sql = "UPDATE student SET qr_code = ? WHERE student_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, path);
                stmt.setString(2, studentID);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
