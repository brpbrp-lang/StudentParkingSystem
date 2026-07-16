package com.parking.controller;

import com.parking.database.DBConnection;
import com.parking.model.Vehicle;
import com.parking.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VehicleController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colVehicleId;
    @FXML private TableColumn<Vehicle, String> colStudentId;
    @FXML private TableColumn<Vehicle, String> colPlate;
    @FXML private TableColumn<Vehicle, String> colBrand;
    @FXML private TableColumn<Vehicle, String> colColor;
    @FXML private TableColumn<Vehicle, String> colType;

    @FXML private TextField studentIdField;
    @FXML private TextField plateField;
    @FXML private TextField brandField;
    @FXML private TextField colorField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField searchField;

    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private Integer selectedVehicleId;

    @FXML
    public void initialize() {
        colVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        colPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        typeCombo.setItems(FXCollections.observableArrayList("Car", "Motorcycle", "Bicycle", "Van", "Other"));

        vehicleTable.setItems(vehicleList);
        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        loadVehicles();
    }

    private void loadVehicles() {
        vehicleList.clear();
        String sql = "SELECT * FROM vehicle ORDER BY vehicle_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicleList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not load vehicles: " + e.getMessage());
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("student_id"),
                rs.getString("plate_number"),
                rs.getString("brand"),
                rs.getString("color"),
                rs.getString("type")
        );
    }

    private void populateForm(Vehicle vehicle) {
        selectedVehicleId = vehicle.getVehicleID();
        studentIdField.setText(vehicle.getStudentID());
        plateField.setText(vehicle.getPlateNumber());
        brandField.setText(vehicle.getBrand());
        colorField.setText(vehicle.getColor());
        typeCombo.setValue(vehicle.getType());
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            return;
        }

        if (!studentExists(studentIdField.getText().trim())) {
            AlertHelper.showWarning("Invalid Student", "No student found with that Student ID. Please add the student first.");
            return;
        }

        String sql = "INSERT INTO vehicle (student_id, plate_number, brand, color, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIdField.getText().trim());
            stmt.setString(2, plateField.getText().trim());
            stmt.setString(3, brandField.getText().trim());
            stmt.setString(4, colorField.getText().trim());
            stmt.setString(5, typeCombo.getValue());

            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Vehicle added successfully.");
            clearForm();
            loadVehicles();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not add vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedVehicleId == null) {
            AlertHelper.showWarning("No Selection", "Please select a vehicle to update.");
            return;
        }
        if (!validateForm()) {
            return;
        }

        String sql = "UPDATE vehicle SET student_id = ?, plate_number = ?, brand = ?, color = ?, type = ? WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIdField.getText().trim());
            stmt.setString(2, plateField.getText().trim());
            stmt.setString(3, brandField.getText().trim());
            stmt.setString(4, colorField.getText().trim());
            stmt.setString(5, typeCombo.getValue());
            stmt.setInt(6, selectedVehicleId);

            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Vehicle updated successfully.");
            clearForm();
            loadVehicles();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not update vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedVehicleId == null) {
            AlertHelper.showWarning("No Selection", "Please select a vehicle to delete.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Confirm Delete", "Delete this vehicle?");
        if (!confirm) {
            return;
        }

        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedVehicleId);
            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Vehicle deleted successfully.");
            clearForm();
            loadVehicles();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error",
                    "Could not delete vehicle. It may have associated parking logs.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadVehicles();
            return;
        }

        vehicleList.clear();
        String sql = "SELECT * FROM vehicle WHERE plate_number LIKE ? OR student_id LIKE ? OR brand LIKE ? ORDER BY vehicle_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicleList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        selectedVehicleId = null;
        studentIdField.clear();
        plateField.clear();
        brandField.clear();
        colorField.clear();
        typeCombo.setValue(null);
        vehicleTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (studentIdField.getText() == null || studentIdField.getText().trim().isEmpty()
                || plateField.getText() == null || plateField.getText().trim().isEmpty()
                || brandField.getText() == null || brandField.getText().trim().isEmpty()
                || typeCombo.getValue() == null) {
            AlertHelper.showWarning("Missing Fields", "Please fill in all required fields.");
            return false;
        }
        return true;
    }

    private boolean studentExists(String studentId) {
        String sql = "SELECT 1 FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
