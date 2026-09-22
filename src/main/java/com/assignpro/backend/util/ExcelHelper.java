package com.assignpro.backend.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;

import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadStatus;

public class ExcelHelper {

    public static boolean hasValidFormat(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        if (filename == null)
            return false;

        filename = filename.toLowerCase();

        return filename.endsWith(".xlsx") ||
                filename.endsWith(".xls") ||
                filename.endsWith(".csv") ||
                "text/csv".equals(contentType) ||
                "application/vnd.ms-excel".equals(contentType) ||
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType);
    }

    public static List<Lead> fileToLeads(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename().toLowerCase();
            if (filename.endsWith(".csv") || "text/csv".equals(file.getContentType())) {
                return csvToLeads(file.getInputStream());
            } else {
                return excelToLeads(file.getInputStream());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse file", e);
        }
    }

    private static List<Lead> csvToLeads(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVReader csvReader = new CSVReader(fileReader)) {

            List<String[]> records = csvReader.readAll();
            List<Lead> leads = new ArrayList<>();

            for (int i = 1; i < records.size(); i++) { // Skip header
                String[] record = records.get(i);

                // Skip completely empty rows
                if (record.length == 0)
                    continue;

                String name = record.length > 0 ? record[0].trim() : "";
                String mobile = record.length > 1 ? record[1].trim() : "";

                // Do not skip empty rows here, let LeadService validate them
                if (name.isEmpty())
                    name = "";
                if (mobile.isEmpty())
                    mobile = "";

                Lead lead = new Lead();
                lead.setName(name);
                lead.setMobile(mobile);
                if (record.length > 2)
                    lead.setEmail(record[2].trim());
                if (record.length > 3)
                    lead.setCompany(record[3].trim());
                if (record.length > 4)
                    lead.setCity(record[4].trim());
                if (record.length > 5)
                    lead.setState(record[5].trim());
                if (record.length > 6)
                    lead.setCountry(record[6].trim());
                if (record.length > 7)
                    lead.setSource(record[7].trim());

                lead.setStatus(LeadStatus.PENDING);
                leads.add(lead);
            }
            return leads;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file", e);
        }
    }

    private static List<Lead> excelToLeads(InputStream is) {
        try {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<Lead> leads = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip Header
                if (rowNumber++ == 0) {
                    continue;
                }

                Lead lead = new Lead();
                for (int cellIdx = 0; cellIdx < 8; cellIdx++) {
                    Cell cell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    switch (cellIdx) {
                        case 0:
                            lead.setName(getCellValue(cell));
                            break;
                        case 1:
                            lead.setMobile(getCellValue(cell));
                            break;
                        case 2:
                            lead.setEmail(getCellValue(cell));
                            break;
                        case 3:
                            lead.setCompany(getCellValue(cell));
                            break;
                        case 4:
                            lead.setCity(getCellValue(cell));
                            break;
                        case 5:
                            lead.setState(getCellValue(cell));
                            break;
                        case 6:
                            lead.setCountry(getCellValue(cell));
                            break;
                        case 7:
                            lead.setSource(getCellValue(cell));
                            break;
                    }
                }

                String nameVal = lead.getName() != null ? lead.getName().trim() : "";
                String mobileVal = lead.getMobile() != null ? lead.getMobile().trim() : "";
                // Do not skip empty rows here, let LeadService validate them
                if (nameVal.isEmpty())
                    lead.setName("");
                if (mobileVal.isEmpty())
                    lead.setMobile("");

                lead.setStatus(LeadStatus.PENDING);
                leads.add(lead);
            }
            workbook.close();
            return leads;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                return getCellValue(evaluator.evaluateInCell(cell));
            case BLANK:
            default:
                return "";
        }
    }
}