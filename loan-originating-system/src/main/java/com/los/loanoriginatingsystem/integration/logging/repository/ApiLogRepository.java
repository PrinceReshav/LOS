package com.los.loanoriginatingsystem.integration.logging.repository;

import com.los.loanoriginatingsystem.integration.logging.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
}
