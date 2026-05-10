package com.los.loanoriginatingsystem.temp.repository;

import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TempLoanApplicationRepository
        extends JpaRepository<TempLoanApplication, String> {

    Optional<TempLoanApplication> findByLeadId(String leadId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TempLoanApplication t WHERE t.id = :id")
    Optional<TempLoanApplication> findByIdForUpdate(String id);

    Optional<TempLoanApplication> findBySubmissionRef(String submissionRef);


    List<TempLoanApplication> findBySubmissionStatus(String status);


}