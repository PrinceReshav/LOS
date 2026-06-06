package com.los.losadminservice.branch.repository;

import com.los.losadminservice.branch.dto.BranchResponse;
import com.los.losadminservice.branch.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {

    Optional<Branch> findByCompanyBranchId(String companyBranchId);

    boolean existsByCompanyBranchId(String companyBranchId);

    List<Branch> findByBranchNameContainingIgnoreCaseOrCompanyBranchIdContaining(
            String name,
            String companyBranchId
    );

    @Query("""
select new
com.los.losadminservice.branch.dto.BranchResponse(
    b.id,
    b.companyBranchId,
    b.branchName,
    b.address,
    b.pincode,
    b.district,
    b.state,
    b.active
)
from Branch b
""")
    List<BranchResponse> findAllResponses();
}