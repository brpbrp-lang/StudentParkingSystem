package com.parking.model;

public class Vehicle {

    private int vehicleID;
    private String studentID;
    private String plateNumber;
    private String brand;
    private String color;
    private String type;

    public Vehicle() {
    }

    public Vehicle(int vehicleID, String studentID, String plateNumber, String brand, String color, String type) {
        this.vehicleID = vehicleID;
        this.studentID = studentID;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.color = color;
        this.type = type;
    }


    public void updateVehicle() {
        // Intentionally left for the controller/service layer to perform the DB update.
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return plateNumber + " (" + brand + ")";
    }
}
