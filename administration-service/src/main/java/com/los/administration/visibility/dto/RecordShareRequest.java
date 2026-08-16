package com.los.administration.visibility.dto;

import com.los.administration.visibility.model.RecordAccessLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordShareRequest {
    @NotBlank private String recordType;
    @NotBlank private String recordId;

    /** Exactly one of sharedWithUserId / sharedWithRoleId should be set. */
    private String sharedWithUserId;
    private String sharedWithRoleId;

    @NotNull private RecordAccessLevel accessLevel;
    private String reason;
}
