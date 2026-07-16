package com.parking.service;

import com.parking.model.ParkingLog;

import java.util.List;

/**
 * Strategy interface implemented by DailyReport, WeeklyReport, and MonthlyReport.
 * Each implementation defines what date range to pull ParkingLog data from.
 */
public interface ReportGenerator {
    List<ParkingLog> generateReport();

    /** Human readable label used for report titles / export file names. */
    String getReportLabel();
}
