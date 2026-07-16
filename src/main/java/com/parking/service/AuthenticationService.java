package com.parking.service;

import com.parking.database.DBConnection;
import com.parking.model.Administrator;
import com.parking.model.ParkingLog;
import com.parking.model.Student;
import com.parking.model.Vehicle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthenticationService {


    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Administrator authenticate(String username, String password) {
        String sql = "SELECT admin_id, username FROM administrator WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Administrator admin = new Administrator(
                            String.valueOf(rs.getInt("admin_id")),
                            rs.getString("username"),
                            null
                    );
                    admin.login();
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Student verifyQR(String qrCode) {
        // Our QR codes simply encode the studentID.
        String sql = "SELECT * FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, qrCode.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("course"),
                            rs.getInt("year_level"),
                            rs.getString("photo"),
                            rs.getString("qr_code")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vehicle verifyVehicle(String studentID) {
        String sql = "SELECT * FROM vehicle WHERE student_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getString("student_id"),
                            rs.getString("plate_number"),
                            rs.getString("brand"),
                            rs.getString("color"),
                            rs.getString("type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ParkingLog checkActiveEntry(String studentID) {
        String sql = "SELECT * FROM parking_log WHERE student_id = ? AND status = 'ENTRY' " +
                     "ORDER BY log_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ParkingLog log = new ParkingLog();
                    log.setLogID(rs.getInt("log_id"));
                    log.setStudentID(rs.getString("student_id"));
                    log.setVehicleID(rs.getInt("vehicle_id"));
                    log.setDate(rs.getDate("date").toLocalDate());
                    log.setTimeIn(rs.getTime("time_in").toLocalTime());
                    log.setStatus(rs.getString("status"));
                    return log;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
