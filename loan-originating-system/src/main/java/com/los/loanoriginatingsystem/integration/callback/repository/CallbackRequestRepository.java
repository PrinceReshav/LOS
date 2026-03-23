package com.los.loanoriginatingsystem.integration.callback.repository;

import com.los.loanoriginatingsystem.integration.callback.entity.CallbackRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CallbackRequestRepository
        extends JpaRepository<CallbackRequest, Long> {

    Optional<CallbackRequest> findByRequestId(String requestId);

}