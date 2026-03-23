package com.los.administration.user.bulk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadPreviewResponse {

    private String uploadId;
    private int totalRecords;
    private int validRecords;
    private int invalidRecords;
    private List<BulkUploadPreviewRow> rows;
}
