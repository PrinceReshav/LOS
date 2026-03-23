package com.los.administration.user.bulk;

import com.los.administration.user.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkUserUploadResult {

    private int totalRecords;
    private int successCount;
    private int failureCount;

    private boolean preview;
    private BulkUploadMode mode;

    private List<UserResponse> successUsers;
    private List<BulkUploadError> errors;

    private String errorFileId;
}