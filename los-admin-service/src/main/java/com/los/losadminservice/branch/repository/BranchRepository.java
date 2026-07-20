package com.los.losadminservice.branch.repository;

import com.los.losadminservice.branch.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {

    Optional<Branch> findByCompanyBranchId(String companyBranchId);

    boolean existsByCompanyBranchId(String companyBranchId);

    List<Branch> findByBranchNameContainingIgnoreCaseOrCompanyBranchIdContaining(
            String name,
            String companyBranchId
    );

    List<Branch> findByActiveTrue();
}
