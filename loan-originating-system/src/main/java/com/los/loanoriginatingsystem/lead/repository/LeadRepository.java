package com.los.loanoriginatingsystem.lead.repository;

import com.los.loanoriginatingsystem.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, String> {

    Optional<Lead> findByLeadNumber(String leadNumber);

    Optional<Lead> findByMobileNumber(String mobileNumber);
}