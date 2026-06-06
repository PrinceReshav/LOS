package com.los.losadminservice.employee.dto;

import com.los.losadminservice.employee.model.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeTeamResponse {

    private Employee employee;

    private List<Employee> managerChain;

    private List<Employee> peers;

    private List<Employee> subordinates;


}