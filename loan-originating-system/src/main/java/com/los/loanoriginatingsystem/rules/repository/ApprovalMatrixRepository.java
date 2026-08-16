package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.ApprovalMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalMatrixRepository extends JpaRepository<ApprovalMatrix, String> {



    List<ApprovalMatrix> findByLevelOrderBySequenceAsc(Integer deviationLevel);

    List<ApprovalMatrix> findByLevelAndActiveTrueOrderBySequenceAsc(Integer deviationLevel);
}
