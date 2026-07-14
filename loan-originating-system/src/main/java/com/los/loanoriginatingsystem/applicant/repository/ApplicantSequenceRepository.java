package com.los.loanoriginatingsystem.applicant.repository;

import com.los.loanoriginatingsystem.applicant.entity.ApplicantSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ApplicantSequenceRepository
        extends JpaRepository<ApplicantSequence,String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a
            from ApplicantSequence a
            where a.code=:code
            """)
    ApplicantSequence getForUpdate(
            @Param("code") String code
    );
}