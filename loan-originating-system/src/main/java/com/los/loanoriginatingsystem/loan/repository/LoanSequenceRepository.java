package com.los.loanoriginatingsystem.loan.repository;

import com.los.loanoriginatingsystem.loan.entity.LoanSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface LoanSequenceRepository
        extends JpaRepository<LoanSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM LoanSequence s WHERE s.name = :name")
    LoanSequence getForUpdate(String name);
}