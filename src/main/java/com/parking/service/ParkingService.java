package com.parking.service;

import com.parking.database.DBConnection;
import com.parking.model.ParkingLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles creation and updating of ParkingLog records for the Scanner Kiosk workflow,
 * as well as general log queries used by the Parking Logs and Reports modules.
 */
public class ParkingService {

    /** Creates a new ENTRY log for a student/vehicle pair. */
    public boolean recordEntry(String studentID, int vehicleID) {
        String sql = "INSERT INTO parking_log (student_id, vehicle_id, date, time_in, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            stmt.setInt(2, vehicleID);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
            stmt.setString(5, ParkingLog.STATUS_ENTRY);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Closes an open log by recording the time-out and marking it as EXIT. */
    public boolean recordExit(int logID) {
        String sql = "UPDATE parking_log SET time_out = ?, status = ? WHERE log_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTime(1, java.sql.Time.valueOf(LocalTime.now()));
            stmt.setString(2, ParkingLog.STATUS_EXIT);
            stmt.setInt(3, logID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Generic status updater, kept for UML completeness / manual admin correction of logs. */
    public boolean updateStatus(int logID, String status) {
        String sql = "UPDATE parking_log SET status = ? WHERE log_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, logID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Returns all parking logs (joined with student name / plate number), most recent first. */
    public List<ParkingLog> getAllLogs() {
        return runLogQuery("SELECT pl.*, s.name AS student_name, v.plate_number FROM parking_log pl " +
                "JOIN student s ON pl.student_id = s.student_id " +
                "JOIN vehicle v ON pl.vehicle_id = v.vehicle_id " +
                "ORDER BY pl.log_id DESC", null);
    }

    /** Filters logs by exact date. */
    public List<ParkingLog> getLogsByDate(LocalDate date) {
        return runLogQuery("SELECT pl.*, s.name AS student_name, v.plate_number FROM parking_log pl " +
                "JOIN student s ON pl.student_id = s.student_id " +
                "JOIN vehicle v ON pl.vehicle_id = v.vehicle_id " +
                "WHERE pl.date = ? ORDER BY pl.log_id DESC", java.sql.Date.valueOf(date));
    }

    /** Filters logs by student ID or name (partial match). */
    public List<ParkingLog> getLogsByStudent(String keyword) {
        String sql = "SELECT pl.*, s.name AS student_name, v.plate_number FROM parking_log pl " +
                "JOIN student s ON pl.student_id = s.student_id " +
                "JOIN vehicle v ON pl.vehicle_id = v.vehicle_id " +
                "WHERE pl.student_id LIKE ? OR s.name LIKE ? ORDER BY pl.log_id DESC";
        List<ParkingLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    /** Filters logs by vehicle plate number (partial match). */
    public List<ParkingLog> getLogsByVehicle(String plateKeyword) {
        String sql = "SELECT pl.*, s.name AS student_name, v.plate_number FROM parking_log pl " +
                "JOIN student s ON pl.student_id = s.student_id " +
                "JOIN vehicle v ON pl.vehicle_id = v.vehicle_id " +
                "WHERE v.plate_number LIKE ? ORDER BY pl.log_id DESC";
        List<ParkingLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + plateKeyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    /** Logs between two dates inclusive (used by Reports module). */
    public List<ParkingLog> getLogsBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT pl.*, s.name AS student_name, v.plate_number FROM parking_log pl " +
                "JOIN student s ON pl.student_id = s.student_id " +
                "JOIN vehicle v ON pl.vehicle_id = v.vehicle_id " +
                "WHERE pl.date BETWEEN ? AND ? ORDER BY pl.log_id DESC";
        List<ParkingLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public int countStudentsInside() {
        String sql = "SELECT COUNT(*) FROM parking_log WHERE status = 'ENTRY'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTodaysEntries() {
        String sql = "SELECT COUNT(*) FROM parking_log WHERE date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<ParkingLog> runLogQuery(String sql, java.sql.Date dateParam) {
        List<ParkingLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (dateParam != null) {
                stmt.setDate(1, dateParam);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    private ParkingLog mapRow(ResultSet rs) throws SQLException {
        ParkingLog log = new ParkingLog();
        log.setLogID(rs.getInt("log_id"));
        log.setStudentID(rs.getString("student_id"));
        log.setVehicleID(rs.getInt("vehicle_id"));
        log.setDate(rs.getDate("date").toLocalDate());
        log.setTimeIn(rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null);
        log.setTimeOut(rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null);
        log.setStatus(rs.getString("status"));
        log.setStudentName(rs.getString("student_name"));
        log.setPlateNumber(rs.getString("plate_number"));
        return log;
    }
}
