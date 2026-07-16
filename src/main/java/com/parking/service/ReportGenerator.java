package com.parking.service;

import com.parking.model.ParkingLog;

import java.util.List;


public interface ReportGenerator {
    List<ParkingLog> generateReport();
    String getReportLabel();
}
