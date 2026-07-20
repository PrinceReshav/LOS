package com.los.losadminservice.employeeBranchMapping.handler;

import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingCreateRequest;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.service.EmployeeBranchMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeBranchMappingHandler {

    private final EmployeeBranchMappingService service;

    public EmployeeBranchMapping create(EmployeeBranchMappingCreateRequest req) {
        return service.create(req);
    }

    public List<EmployeeBranchMappingResponse> getAll() {
        return service.getAll();
    }

    public List<EmployeeBranchMappingResponse> getForEmployee(String employeeId) {
        return service.getResponsesForEmployee(employeeId);
    }

    public EmployeeBranchMappingResponse getById(Long id) {
        return service.getResponseById(id);
    }

    public void deactivate(Long id) {
        service.deactivate(id);
    }

    public EmployeeBranchMapping setPrimary(Long id) {
        return service.setPrimary(id);
    }
}
