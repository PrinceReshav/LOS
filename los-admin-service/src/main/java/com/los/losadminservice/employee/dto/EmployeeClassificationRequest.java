package com.los.losadminservice.employee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Used by HR/Admin to assign or change an employee's Department and
 * Designation - the two classification fields that don't arrive via the
 * User-service gRPC/Kafka event and must be set inside losAdminService
 * before a manager or branch can be assigned.
 */
@Data
public class EmployeeClassificationRequest {

    @NotBlank
    private String departmentId;

    @NotBlank
    private String designationId;
}
