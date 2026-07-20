package com.los.losadminservice.designation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DesignationRequest {

    @NotBlank
    private String designationId;

    @NotBlank
    private String name;

    private String departmentCode;

    private Boolean active;
}
