package com.los.administration.user.excel;

import com.los.administration.user.dto.UserCreateRequest;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Component
public class UserExcelParser implements ExcelParser<UserCreateRequest> {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "username",
            "email",
            "mobile",
            "alias",
            "firstName",
            "lastName",
            "employeeId",
            "roleName",
            "profileName"
    );

    @Override
    public List<UserCreateRequest> parse(MultipartFile file) {

        List<UserCreateRequest> users = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Integer> headerMap = extractHeaders(sheet.getRow(0));

            validateHeaders(headerMap);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                UserCreateRequest req = new UserCreateRequest();
                req.setUsername(get(row, headerMap, "username"));
                req.setEmail(
                        Optional.ofNullable(get(row, headerMap, "email"))
                                .map(String::toLowerCase)
                                .orElse(null)
                );
                req.setMobile(get(row, headerMap, "mobile"));
                req.setAlias(get(row, headerMap, "alias"));
                req.setFirstName(get(row, headerMap, "firstName"));
                req.setMiddleName(get(row, headerMap, "middleName"));
                req.setLastName(get(row, headerMap, "lastName"));
                req.setEmployeeId(get(row, headerMap, "employeeId"));
                req.setRoleName(get(row, headerMap, "roleName"));
                req.setProfileName(get(row, headerMap, "profileName"));

                users.add(req);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Excel file", e);
        }

        return users;
    }

    private Map<String, Integer> extractHeaders(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            map.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }
        return map;
    }

    private void validateHeaders(Map<String, Integer> headers) {
        for (String required : REQUIRED_HEADERS) {
            if (!headers.containsKey(required)) {
                throw new IllegalArgumentException(
                        String.format("Missing required column: %s", required)
                );
            }
        }
    }

    private String get(Row row, Map<String, Integer> map, String key) {
        Integer idx = map.get(key);
        if (idx == null) return null;
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString().trim();
    }
}


/*
*@Component
public class UserExcelParser implements ExcelParser<UserCreateRequest> {

    @Override
    public List<UserCreateRequest> parse(MultipartFile file) {

        List<UserCreateRequest> users = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                UserCreateRequest req = new UserCreateRequest();
                req.setUsername(getString(row, 0));
                req.setEmail(getString(row, 1));
                req.setMobile(getString(row, 2));
                req.setAlias(getString(row, 3));
                req.setFirstName(getString(row, 4));
                req.setMiddleName(getString(row, 5));
                req.setLastName(getString(row, 6));
                req.setEmployeeId(getString(row, 7));
                req.setRoleName(getString(row, 8));
                req.setProfileName(getString(row, 9));

                users.add(req);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Excel file format", e);
        }

        return users;
    }

    private String getString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString().trim();
    }
}
*/