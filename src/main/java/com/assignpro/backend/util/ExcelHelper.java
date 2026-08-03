package com.assignpro.backend.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadStatus;

public class ExcelHelper {

    public static final String TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Lead> excelToLeads(InputStream is) {

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

                    Cell cell = currentRow.getCell(cellIdx,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

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

                // Default Status
                lead.setStatus(LeadStatus.NEW);

                leads.add(lead);
            }

            workbook.close();

            return leads;

        } catch (IOException e) {

            throw new RuntimeException("Failed to parse Excel file", e);
        }
    }

    private static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

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

                FormulaEvaluator evaluator =
                        cell.getSheet()
                            .getWorkbook()
                            .getCreationHelper()
                            .createFormulaEvaluator();

                return getCellValue(evaluator.evaluateInCell(cell));

            case BLANK:
            default:
                return "";
        }
    }
}