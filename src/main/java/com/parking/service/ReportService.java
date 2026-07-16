package com.parking.service;

import com.parking.model.ParkingLog;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Uses a ReportGenerator strategy (Daily/Weekly/Monthly) to pull data, then exports
 * the resulting ParkingLog list to either PDF (Apache PDFBox) or Excel (Apache POI).
 */
public class ReportService {

    private static final String EXPORT_FOLDER = "src/main/resources/exports/";

    public List<ParkingLog> generate(ReportGenerator generator) {
        return generator.generateReport();
    }

    /** Exports the given logs to a PDF file. Returns the absolute path of the created file. */
    public String exportToPDF(List<ParkingLog> logs, String reportLabel) throws IOException {
        ensureExportFolder();
        String filePath = EXPORT_FOLDER + reportLabel + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
            PDType1Font bodyFont = PDType1Font.HELVETICA;

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 16f;

            content.beginText();
            content.setFont(titleFont, 16);
            content.newLineAtOffset(margin, y);
            content.showText(reportLabel.replace("_", " "));
            content.endText();
            y -= 30;

            String[] headers = {"Log ID", "Student", "Plate No.", "Date", "Time In", "Time Out", "Status"};
            float[] colX = {margin, margin + 60, margin + 190, margin + 290, margin + 360, margin + 420, margin + 480};

            content.setFont(titleFont, 10);
            content.beginText();
            content.newLineAtOffset(margin, y);
            for (int i = 0; i < headers.length; i++) {
                content.newLineAtOffset(i == 0 ? 0 : colX[i] - colX[i - 1], 0);
                content.showText(headers[i]);
            }
            content.endText();
            y -= leading;

            content.setFont(bodyFont, 9);
            for (ParkingLog log : logs) {
                if (y < margin) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - margin;
                }

                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText(String.valueOf(log.getLogID()));
                content.newLineAtOffset(colX[1] - colX[0], 0);
                content.showText(safe(log.getStudentName()));
                content.newLineAtOffset(colX[2] - colX[1], 0);
                content.showText(safe(log.getPlateNumber()));
                content.newLineAtOffset(colX[3] - colX[2], 0);
                content.showText(String.valueOf(log.getDate()));
                content.newLineAtOffset(colX[4] - colX[3], 0);
                content.showText(log.getTimeIn() != null ? log.getTimeIn().toString() : "-");
                content.newLineAtOffset(colX[5] - colX[4], 0);
                content.showText(log.getTimeOut() != null ? log.getTimeOut().toString() : "-");
                content.newLineAtOffset(colX[6] - colX[5], 0);
                content.showText(safe(log.getStatus()));
                content.endText();

                y -= leading;
            }

            content.close();
            document.save(filePath);
        }

        return filePath;
    }

    /** Exports the given logs to an Excel (.xlsx) file. Returns the absolute path of the created file. */
    public String exportToExcel(List<ParkingLog> logs, String reportLabel) throws IOException {
        ensureExportFolder();
        String filePath = EXPORT_FOLDER + reportLabel + ".xlsx";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(reportLabel);

            String[] headers = {"Log ID", "Student ID", "Student Name", "Plate Number", "Date", "Time In", "Time Out", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ParkingLog log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getLogID());
                row.createCell(1).setCellValue(safe(log.getStudentID()));
                row.createCell(2).setCellValue(safe(log.getStudentName()));
                row.createCell(3).setCellValue(safe(log.getPlateNumber()));
                row.createCell(4).setCellValue(String.valueOf(log.getDate()));
                row.createCell(5).setCellValue(log.getTimeIn() != null ? log.getTimeIn().toString() : "-");
                row.createCell(6).setCellValue(log.getTimeOut() != null ? log.getTimeOut().toString() : "-");
                row.createCell(7).setCellValue(safe(log.getStatus()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }

        return filePath;
    }

    private void ensureExportFolder() throws IOException {
        java.nio.file.Path folder = java.nio.file.Paths.get(EXPORT_FOLDER);
        if (!java.nio.file.Files.exists(folder)) {
            java.nio.file.Files.createDirectories(folder);
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
