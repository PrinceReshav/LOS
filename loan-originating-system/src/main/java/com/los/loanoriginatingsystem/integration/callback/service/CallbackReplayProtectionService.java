package com.los.loanoriginatingsystem.integration.callback.service;

import com.los.loanoriginatingsystem.integration.callback.entity.CallbackRequest;
import com.los.loanoriginatingsystem.integration.callback.repository.CallbackRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallbackReplayProtectionService {

    private final CallbackRequestRepository repository;

    public boolean isReplay(String requestId) {

        return repository.findByRequestId(requestId).isPresent();
    }

    public void markProcessed(String requestId) {

        CallbackRequest request = new CallbackRequest();
        request.setRequestId(requestId);

        repository.save(request);
    }
}