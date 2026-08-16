package com.los.loanoriginatingsystem.insurance.repository;

import com.los.loanoriginatingsystem.insurance.entity.PropertyInsuranceRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyInsuranceRateRepository extends JpaRepository<PropertyInsuranceRate, String> {

    Optional<PropertyInsuranceRate> findByPolicyTenureMonthsAndActiveTrue(Integer tenureMonths);

    List<PropertyInsuranceRate> findAllByActiveTrue();
}
