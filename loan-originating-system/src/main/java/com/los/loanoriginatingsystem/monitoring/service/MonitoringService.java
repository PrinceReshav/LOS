package com.los.loanoriginatingsystem.monitoring.service;

import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final TempLoanApplicationRepository repo;

    public long total() {
        return repo.count();
    }

    public long completed() {
        return repo.findBySubmissionStatus("COMPLETED").size();
    }

    public long failed() {
        return repo.findBySubmissionStatus("FAILED").size();
    }

    public long failedFinal() {
        return repo.findBySubmissionStatus("FAILED_FINAL").size();
    }

    public long processing() {
        return repo.findBySubmissionStatus("PROCESSING").size();
    }
}