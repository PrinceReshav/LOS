package com.los.loanoriginatingsystem.document.repository;

import com.los.loanoriginatingsystem.document.entity.DocumentChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChecklistRepository
        extends JpaRepository<DocumentChecklist, String> {

    List<DocumentChecklist> findByLoanProductCode(
            String loanProductCode
    );
}