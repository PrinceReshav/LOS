package com.los.loanoriginatingsystem.loan.service;

import com.los.loanoriginatingsystem.loan.entity.LoanSequence;
import com.los.loanoriginatingsystem.loan.repository.LoanSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanNumberService{

    private final LoanSequenceRepository repo;

    @Transactional
    public String generateLAN() {

        LoanSequence seq = repo.getForUpdate("LAN");

        Long next = seq.getValue() + 1;
        seq.setValue(next);

        repo.save(seq);

        return "LAN-" + String.format("%08d", next);
    }
}