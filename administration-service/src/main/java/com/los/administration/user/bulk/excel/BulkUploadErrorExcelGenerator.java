package com.los.administration.user.bulk.excel;

import com.los.administration.user.bulk.BulkUploadError;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class BulkUploadErrorExcelGenerator {

    public ByteArrayInputStream generate(List<BulkUploadError> errors) {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.createSheet("Errors");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Row Number");
            header.createCell(1).setCellValue("Field");
            header.createCell(2).setCellValue("Error Message");
            header.createCell(3).setCellValue("Raw Value");

            int rowIdx = 1;
            for (BulkUploadError error : errors) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(error.getRowNumber());
                row.createCell(1).setCellValue(error.getField());
                row.createCell(2).setCellValue(error.getMessage());
                row.createCell(3).setCellValue(error.getRawValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate error Excel", e);
        }
    }
}