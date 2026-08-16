package com.los.losadminservice.designation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignationResponse {

    private String designationId;
    private String name;
    private String departmentCode;
    private Boolean active;
}
