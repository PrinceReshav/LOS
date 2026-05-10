package com.los.loanoriginatingsystem.lead.repository;

import com.los.loanoriginatingsystem.lead.sequence.LeadSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface LeadSequenceRepository extends JpaRepository<LeadSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM LeadSequence s WHERE s.name = :name")
    LeadSequence getForUpdate(String name);
}