package com.parking.controller;

import com.parking.database.DBConnection;
import com.parking.model.Student;
import com.parking.service.QRService;
import com.parking.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private TableColumn<Student, Integer> colYear;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField courseField;
    @FXML private TextField yearField;
    @FXML private TextField searchField;

    @FXML private ImageView photoView;
    @FXML private ImageView qrView;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final QRService qrService = new QRService();

    private String selectedPhotoPath;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));

        studentTable.setItems(studentList);
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        loadStudents();
    }

    private void loadStudents() {
        studentList.clear();
        String sql = "SELECT * FROM student ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not load students: " + e.getMessage());
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("course"),
                rs.getInt("year_level"),
                rs.getString("photo"),
                rs.getString("qr_code")
        );
    }

    private void populateForm(Student student) {
        idField.setText(student.getStudentID());
        idField.setDisable(true); // primary key shouldn't change on update
        nameField.setText(student.getName());
        courseField.setText(student.getCourse());
        yearField.setText(String.valueOf(student.getYearLevel()));

        selectedPhotoPath = student.getPhoto();
        setImageOrPlaceholder(photoView, student.getPhoto());
        setImageOrPlaceholder(qrView, student.getQrCode());
    }

    private void setImageOrPlaceholder(ImageView view, String path) {
        try {
            if (path != null && Files.exists(Paths.get(path))) {
                view.setImage(new Image(new File(path).toURI().toString()));
            } else {
                view.setImage(null);
            }
        } catch (Exception e) {
            view.setImage(null);
        }
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Student Photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(photoView.getScene().getWindow());
        if (file != null) {
            try {
                Path targetDir = Paths.get("src/main/resources/photos");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                String idText = idField.getText().trim();
                String ext = file.getName().substring(file.getName().lastIndexOf('.'));
                Path target = targetDir.resolve((idText.isEmpty() ? "temp" : idText) + ext);
                Files.copy(file.toPath(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                selectedPhotoPath = target.toString();
                photoView.setImage(new Image(target.toUri().toString()));
            } catch (IOException e) {
                e.printStackTrace();
                AlertHelper.showError("File Error", "Could not save the selected photo.");
            }
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            return;
        }
        String sql = "INSERT INTO student (student_id, name, course, year_level, photo, qr_code) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idField.getText().trim());
            stmt.setString(2, nameField.getText().trim());
            stmt.setString(3, courseField.getText().trim());
            stmt.setInt(4, Integer.parseInt(yearField.getText().trim()));
            stmt.setString(5, selectedPhotoPath);
            stmt.setString(6, null);

            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Student added successfully.");
            clearForm();
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not add student: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a student to update.");
            return;
        }
        if (!validateForm()) {
            return;
        }

        String sql = "UPDATE student SET name = ?, course = ?, year_level = ?, photo = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, courseField.getText().trim());
            stmt.setInt(3, Integer.parseInt(yearField.getText().trim()));
            stmt.setString(4, selectedPhotoPath);
            stmt.setString(5, selected.getStudentID());

            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Student updated successfully.");
            clearForm();
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Could not update student: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a student to delete.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Confirm Delete",
                "Delete student " + selected.getStudentID() + "? This will also remove their vehicles and parking logs.");
        if (!confirm) {
            return;
        }

        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selected.getStudentID());
            stmt.executeUpdate();
            AlertHelper.showInfo("Success", "Student deleted successfully.");
            clearForm();
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error",
                    "Could not delete student. Make sure ON DELETE CASCADE is set, or remove their vehicles/logs first.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }

        studentList.clear();
        String sql = "SELECT * FROM student WHERE student_id LIKE ? OR name LIKE ? OR course LIKE ? ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    studentList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateQR() {

        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertHelper.showWarning(
                    "No Selection",
                    "Please select a student to generate a QR code for."
            );
            return;
        }

        String path = qrService.generateAndSaveQRCode(selected.getStudentID());
        selected.setQrCode(path);

        if (path != null) {

            setImageOrPlaceholder(qrView, path);
            loadStudents();

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/QRPreview.fxml"));

                Parent root = loader.load();

                QRPreviewController controller = loader.getController();

                controller.setData(selected);

                Stage stage = new Stage();

                stage.setTitle("QR Code Preview");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError(
                        "Preview Error",
                        "Unable to open QR Preview."
                );
            }

        } else {

            AlertHelper.showError(
                    "QR Error",
                    "Failed to generate QR Code."
            );

        }

    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        idField.clear();
        idField.setDisable(false);
        nameField.clear();
        courseField.clear();
        yearField.clear();
        selectedPhotoPath = null;
        photoView.setImage(null);
        qrView.setImage(null);
        studentTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (idField.getText() == null || idField.getText().trim().isEmpty()
                || nameField.getText() == null || nameField.getText().trim().isEmpty()
                || courseField.getText() == null || courseField.getText().trim().isEmpty()
                || yearField.getText() == null || yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Missing Fields", "Please fill in all required fields.");
            return false;
        }
        try {
            Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Invalid Input", "Year level must be a number.");
            return false;
        }
        return true;
    }
}
