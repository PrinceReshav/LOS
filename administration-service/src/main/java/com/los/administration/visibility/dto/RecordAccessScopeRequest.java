package com.los.administration.visibility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecordAccessScopeRequest {
    @NotBlank private String requestingUserId;
    @NotBlank private String recordType;
}
