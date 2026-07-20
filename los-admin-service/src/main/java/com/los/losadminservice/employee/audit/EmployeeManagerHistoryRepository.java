package com.los.losadminservice.employee.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeManagerHistoryRepository
        extends JpaRepository<EmployeeManagerHistory, Long> {

    List<EmployeeManagerHistory> findByEmployeeIdOrderByChangedAtDesc(String employeeId);
}
