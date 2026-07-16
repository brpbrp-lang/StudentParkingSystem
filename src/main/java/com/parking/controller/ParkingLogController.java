package com.parking.controller;

import com.parking.model.ParkingLog;
import com.parking.service.ParkingService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ParkingLogController {

    @FXML private TableView<ParkingLog> logTable;
    @FXML private TableColumn<ParkingLog, Integer> colLogId;
    @FXML private TableColumn<ParkingLog, String> colStudent;
    @FXML private TableColumn<ParkingLog, String> colPlate;
    @FXML private TableColumn<ParkingLog, String> colDate;
    @FXML private TableColumn<ParkingLog, String> colTimeIn;
    @FXML private TableColumn<ParkingLog, String> colTimeOut;
    @FXML private TableColumn<ParkingLog, String> colStatus;

    @FXML private DatePicker datePicker;
    @FXML private TextField studentSearchField;
    @FXML private TextField vehicleSearchField;

    private final ObservableList<ParkingLog> logList = FXCollections.observableArrayList();
    private final ParkingService parkingService = new ParkingService();

    @FXML
    public void initialize() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logID"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTimeIn.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        colTimeOut.setCellValueFactory(new PropertyValueFactory<>("timeOut"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        logTable.setItems(logList);
        refresh();
    }

    private void refresh() {
        logList.setAll(parkingService.getAllLogs());
    }

    @FXML
    private void handleFilterByDate() {
        if (datePicker.getValue() == null) {
            refresh();
            return;
        }
        logList.setAll(parkingService.getLogsByDate(datePicker.getValue()));
    }

    @FXML
    private void handleFilterByStudent() {
        String keyword = studentSearchField.getText() == null ? "" : studentSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refresh();
            return;
        }
        logList.setAll(parkingService.getLogsByStudent(keyword));
    }

    @FXML
    private void handleFilterByVehicle() {
        String keyword = vehicleSearchField.getText() == null ? "" : vehicleSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refresh();
            return;
        }
        logList.setAll(parkingService.getLogsByVehicle(keyword));
    }

    @FXML
    private void handleClearFilters() {
        datePicker.setValue(null);
        studentSearchField.clear();
        vehicleSearchField.clear();
        refresh();
    }
}
