package com.los.loanoriginatingsystem.report.repository;

import com.los.loanoriginatingsystem.report.entity.ReportFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportFolderRepository
        extends JpaRepository<ReportFolder, String> {
}
