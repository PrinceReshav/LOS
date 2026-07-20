package com.los.loanoriginatingsystem.report.repository;

import com.los.loanoriginatingsystem.report.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDefinitionRepository
        extends JpaRepository<ReportDefinition, String> {

    List<ReportDefinition> findByFolderId(String folderId);
}
