package com.los.loanoriginatingsystem.applicant.service;

import com.los.loanoriginatingsystem.applicant.entity.ApplicantSequence;
import com.los.loanoriginatingsystem.applicant.repository.ApplicantSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantNumberService {

    private final ApplicantSequenceRepository repo;

    @Transactional
    public String generateApplicantNumber() {

        ApplicantSequence seq =
                repo.getForUpdate("APP");

        Long next =
                seq.getValue() + 1;

        seq.setValue(next);

        repo.save(seq);

        return "APP-"
                + String.format(
                "%08d",
                next
        );
    }
}