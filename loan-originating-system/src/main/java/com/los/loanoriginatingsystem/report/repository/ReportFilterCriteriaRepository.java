package com.los.loanoriginatingsystem.report.repository;

import com.los.loanoriginatingsystem.report.entity.ReportFilterCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportFilterCriteriaRepository
        extends JpaRepository<ReportFilterCriteria, String> {

    List<ReportFilterCriteria> findByReportId(String reportId);

    void deleteByReportId(String reportId);
}
