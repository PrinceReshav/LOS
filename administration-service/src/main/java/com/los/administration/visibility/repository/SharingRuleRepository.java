package com.los.administration.visibility.repository;

import com.los.administration.visibility.model.SharingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharingRuleRepository
        extends JpaRepository<SharingRule, Long> {

    List<SharingRule> findByFromRoleIdAndActiveTrue(String fromRoleId);
}