package com.los.loanoriginatingsystem.insurance.repository;

import com.los.loanoriginatingsystem.insurance.entity.InsuranceMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsuranceMatrixRepository extends JpaRepository<InsuranceMatrix, String> {

    @org.springframework.data.jpa.repository.Query("""
        select m from InsuranceMatrix m
        where m.active = true
          and m.tenureMonths = :tenureMonths
          and :age between m.minAge and m.maxAge
        """)
    Optional<InsuranceMatrix> findApplicableRate(int age, int tenureMonths);

    List<InsuranceMatrix> findAllByActiveTrue();
}
