package com.parking.service;

import com.parking.model.ParkingLog;

import java.time.LocalDate;
import java.util.List;

public class WeeklyReport implements ReportGenerator {

    private final ParkingService parkingService = new ParkingService();

    @Override
    public List<ParkingLog> generateReport() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6); // last 7 days including today
        return parkingService.getLogsBetween(weekStart, today);
    }

    @Override
    public String getReportLabel() {
        return "Weekly_Report_" + LocalDate.now();
    }
}
