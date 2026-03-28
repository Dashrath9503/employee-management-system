package com.emp.management.service;

import com.emp.management.model.Employee;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;

@Service
public class ExportService {

    public ByteArrayOutputStream exportToExcel(List<Employee> employees) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Alternate row style
        CellStyle altStyle = workbook.createCellStyle();
        altStyle.cloneStyleFrom(dataStyle);
        altStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header row
        String[] headers = {"#", "Employee ID", "First Name", "Last Name", "Email",
                             "Phone", "Job Title", "Department", "Salary", "Status", "Join Date"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowNum = 1;
        for (Employee emp : employees) {
            Row row = sheet.createRow(rowNum);
            CellStyle style = (rowNum % 2 == 0) ? altStyle : dataStyle;

            createCell(row, 0, String.valueOf(rowNum), style);
            createCell(row, 1, "EMP-" + String.format("%04d", emp.getId()), style);
            createCell(row, 2, emp.getFirstName(), style);
            createCell(row, 3, emp.getLastName(), style);
            createCell(row, 4, emp.getEmail(), style);
            createCell(row, 5, emp.getPhone() != null ? emp.getPhone() : "-", style);
            createCell(row, 6, emp.getJobTitle() != null ? emp.getJobTitle() : "-", style);
            createCell(row, 7, emp.getDepartment() != null ? emp.getDepartment().getName() : "-", style);
            createCell(row, 8, emp.getSalary() != null ? "Rs." + emp.getSalary() : "-", style);
            createCell(row, 9, emp.getStatus().name(), style);
            createCell(row, 10, emp.getJoinDate() != null ? emp.getJoinDate().toString() : "-", style);
            rowNum++;
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public ByteArrayOutputStream exportToPdf(List<Employee> employees) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD,
                new BaseColor(26, 31, 54));
        Paragraph title = new Paragraph("Employee Management System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        com.itextpdf.text.Font subFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL,
                BaseColor.GRAY);
        Paragraph sub = new Paragraph("Employee List Report", subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        // Table
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 1.2f, 1.3f, 1.3f, 2f, 1.3f, 1.5f, 1.5f, 1f});

        // Table header
        String[] headers = {"#", "Emp ID", "First Name", "Last Name", "Email", "Phone", "Job Title", "Department", "Status"};
        BaseColor headerBg = new BaseColor(26, 31, 54);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Data rows
        com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 8);
        BaseColor altBg = new BaseColor(240, 242, 245);

        int i = 1;
        for (Employee emp : employees) {
            BaseColor bg = (i % 2 == 0) ? altBg : BaseColor.WHITE;
            String[] values = {
                String.valueOf(i),
                "EMP-" + String.format("%04d", emp.getId()),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhone() != null ? emp.getPhone() : "-",
                emp.getJobTitle() != null ? emp.getJobTitle() : "-",
                emp.getDepartment() != null ? emp.getDepartment().getName() : "-",
                emp.getStatus().name()
            };
            for (String val : values) {
                PdfPCell cell = new PdfPCell(new Phrase(val, dataFont));
                cell.setBackgroundColor(bg);
                cell.setPadding(6);
                table.addCell(cell);
            }
            i++;
        }

        document.add(table);

        com.itextpdf.text.Font footFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Total Employees: " + employees.size(), footFont);
        footer.setSpacingBefore(10);
        document.add(footer);

        document.close();
        return out;
    }
}
