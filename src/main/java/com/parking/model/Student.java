package com.parking.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private String studentID;
    private String name;
    private String course;
    private int yearLevel;
    private String photo;      // file path to photo
    private String qrCode;     // file path to generated QR code image

    private final List<Vehicle> vehicles = new ArrayList<>();

    public Student() {
    }

    public Student(String studentID, String name, String course, int yearLevel, String photo, String qrCode) {
        this.studentID = studentID;
        this.name = name;
        this.course = course;
        this.yearLevel = yearLevel;
        this.photo = photo;
        this.qrCode = qrCode;
    }


    public String generateQR() {
        this.qrCode = com.parking.util.QRGenerator.generateQRCode(studentID);
        return this.qrCode;
    }


    public void registerVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return studentID + " - " + name;
    }
}
