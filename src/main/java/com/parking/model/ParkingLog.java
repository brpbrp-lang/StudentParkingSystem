package com.parking.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ParkingLog {

    public static final String STATUS_ENTRY = "ENTRY";
    public static final String STATUS_EXIT = "EXIT";

    private int logID;
    private String studentID;
    private int vehicleID;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private String status;

    // Extra display-only fields (populated via JOIN queries, not stored directly)
    private String studentName;
    private String plateNumber;

    public ParkingLog() {
    }

    public ParkingLog(int logID, String studentID, int vehicleID, LocalDate date,
                       LocalTime timeIn, LocalTime timeOut, String status) {
        this.logID = logID;
        this.studentID = studentID;
        this.vehicleID = vehicleID;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public void recordEntry() {
        this.date = LocalDate.now();
        this.timeIn = LocalTime.now();
        this.status = STATUS_ENTRY;
    }

    public void recordExit() {
        this.timeOut = LocalTime.now();
        this.status = STATUS_EXIT;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
