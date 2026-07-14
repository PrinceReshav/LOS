package com.los.loanoriginatingsystem.lead.service;

import com.los.loanoriginatingsystem.lead.repository.LeadSequenceRepository;
import com.los.loanoriginatingsystem.lead.sequence.LeadSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeadNumberService {

    private final LeadSequenceRepository repo;

    @Transactional
    public String generateLeadNumber() {

        LeadSequence seq =
                repo.getForUpdate("LEAD")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "LEAD sequence not found"
                                )
                        );

        Long next = seq.getValue() + 1;

        seq.setValue(next);

        repo.save(seq);

        return "Lead-" + String.format("%06d", next);
    }
}