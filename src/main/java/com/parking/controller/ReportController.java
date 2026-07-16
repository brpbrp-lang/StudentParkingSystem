package com.parking.controller;

import com.parking.model.ParkingLog;
import com.parking.service.*;
import com.parking.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ReportController {

    @FXML private TableView<ParkingLog> reportTable;
    @FXML private TableColumn<ParkingLog, Integer> colLogId;
    @FXML private TableColumn<ParkingLog, String> colStudent;
    @FXML private TableColumn<ParkingLog, String> colPlate;
    @FXML private TableColumn<ParkingLog, String> colDate;
    @FXML private TableColumn<ParkingLog, String> colTimeIn;
    @FXML private TableColumn<ParkingLog, String> colTimeOut;
    @FXML private TableColumn<ParkingLog, String> colStatus;
    @FXML private Label reportTitleLabel;

    private final ObservableList<ParkingLog> reportList = FXCollections.observableArrayList();
    private final ReportService reportService = new ReportService();

    private ReportGenerator currentGenerator;

    @FXML
    public void initialize() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logID"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTimeIn.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        colTimeOut.setCellValueFactory(new PropertyValueFactory<>("timeOut"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        reportTable.setItems(reportList);
    }

    @FXML
    private void handleDaily() {
        generate(new DailyReport());
    }

    @FXML
    private void handleWeekly() {
        generate(new WeeklyReport());
    }

    @FXML
    private void handleMonthly() {
        generate(new MonthlyReport());
    }

    private void generate(ReportGenerator generator) {
        currentGenerator = generator;
        reportList.setAll(reportService.generate(generator));
        reportTitleLabel.setText(generator.getReportLabel().replace("_", " ") + " (" + reportList.size() + " records)");
    }

    @FXML
    private void handleExportPDF() {
        if (!hasReport()) {
            return;
        }
        try {
            String path = reportService.exportToPDF(reportList, currentGenerator.getReportLabel());
            AlertHelper.showInfo("Export Successful", "PDF report saved to:\n" + path);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Export Failed", "Could not export PDF: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportExcel() {
        if (!hasReport()) {
            return;
        }
        try {
            String path = reportService.exportToExcel(reportList, currentGenerator.getReportLabel());
            AlertHelper.showInfo("Export Successful", "Excel report saved to:\n" + path);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Export Failed", "Could not export Excel: " + e.getMessage());
        }
    }

    private boolean hasReport() {
        if (currentGenerator == null || reportList.isEmpty()) {
            AlertHelper.showWarning("No Report", "Please generate a report first (Daily, Weekly, or Monthly).");
            return false;
        }
        return true;
    }
}
