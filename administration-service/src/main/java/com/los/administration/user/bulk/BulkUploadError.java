package com.los.administration.user.bulk;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkUploadError {

    private int rowNumber;     // Excel row number (1-based)
    private String field;      // email / roleName / profileName etc
    private String message;    // human readable
    private String rawValue;   // value from Excel
}