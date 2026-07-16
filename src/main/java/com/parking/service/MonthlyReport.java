package com.parking.service;

import com.parking.model.ParkingLog;

import java.time.LocalDate;
import java.util.List;

public class MonthlyReport implements ReportGenerator {

    private final ParkingService parkingService = new ParkingService();

    @Override
    public List<ParkingLog> generateReport() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        return parkingService.getLogsBetween(monthStart, today);
    }

    @Override
    public String getReportLabel() {
        return "Monthly_Report_" + LocalDate.now();
    }
}
