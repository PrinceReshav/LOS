package com.los.loanoriginatingsystem.report.repository;

import com.los.loanoriginatingsystem.report.entity.DashboardComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DashboardComponentRepository
        extends JpaRepository<DashboardComponent, String> {

    List<DashboardComponent> findByDashboardId(String dashboardId);

    List<DashboardComponent> findByDashboardIdIn(Collection<String> dashboardIds);

    void deleteByDashboardId(String dashboardId);
}
