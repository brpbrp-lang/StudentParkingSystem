package com.parking.service;

import com.parking.model.ParkingLog;

import java.time.LocalDate;
import java.util.List;

public class DailyReport implements ReportGenerator {

    private final ParkingService parkingService = new ParkingService();

    @Override
    public List<ParkingLog> generateReport() {
        return parkingService.getLogsByDate(LocalDate.now());
    }

    @Override
    public String getReportLabel() {
        return "Daily_Report_" + LocalDate.now();
    }
}
