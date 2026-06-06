package com.los.administration.outbox.repository;

import com.los.administration.outbox.model.OutboxEvent;
import com.los.administration.outbox.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(
            OutboxStatus status
    );
    
    List<OutboxEvent> findByStatusIn(
            List<OutboxStatus> statuses
    );
}