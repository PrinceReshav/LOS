package com.los.loanoriginatingsystem.commercial.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Thin REST client into los-admin-service to resolve "which employees at
 * this branch are eligible to approve as role X" - used once
 * CommercialMatrixService has resolved a required approval role, to
 * actually assign Approver 1 / Approver 2.
 *
 * Equivalent to the old Salesforce SOQL against Employee_Branch_Mapping__c
 * joined to User by Role, done here as a cross-service call since
 * Employee data now lives in los-admin-service instead of the same object
 * graph.
 */
@Component
@Slf4j
public class AdminServiceEmployeeClient {

    private final RestTemplate restTemplate;
    private final String adminServiceUrl;

    public AdminServiceEmployeeClient(
            RestTemplate restTemplate,
            @Value("${admin.service.url}") String adminServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.adminServiceUrl = adminServiceUrl;
    }

    /**
     * Returns employeeIds eligible to approve as roleCode at branchId.
     * Fails soft (empty list + logged warning) rather than throwing, so a
     * downstream admin-service outage blocks approver auto-assignment
     * (leaves the approval PENDING for manual assignment) rather than the
     * whole commercial-matrix resolution flow.
     */
    @SuppressWarnings("unchecked")
    public List<String> findEligibleApproverEmployeeIds(String branchId, String roleCode) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(adminServiceUrl + "/admin/employees/eligible-approvers")
                    .queryParam("branchId", branchId)
                    .queryParam("roleCode", roleCode)
                    .toUriString();

            List<Map<String, Object>> employees = restTemplate.getForObject(url, List.class);

            if (employees == null) return List.of();

            return employees.stream()
                    .map(e -> String.valueOf(e.get("employeeId")))
                    .toList();

        } catch (RestClientException e) {
            log.warn("Failed to resolve eligible approvers from admin-service (branch={}, role={}): {}",
                    branchId, roleCode, e.getMessage());
            return List.of();
        }
    }
}
