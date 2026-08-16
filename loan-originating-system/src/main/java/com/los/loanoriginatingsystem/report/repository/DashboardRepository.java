package com.los.loanoriginatingsystem.report.repository;

import com.los.loanoriginatingsystem.report.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRepository
        extends JpaRepository<Dashboard, String> {
}
