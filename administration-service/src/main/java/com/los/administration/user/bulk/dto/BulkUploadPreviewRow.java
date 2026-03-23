package com.los.administration.user.bulk.dto;

import com.los.administration.user.dto.UserCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadPreviewRow {

    private int rowNumber;
    private UserCreateRequest data;
    private boolean valid;
    private List<String> errors;
}