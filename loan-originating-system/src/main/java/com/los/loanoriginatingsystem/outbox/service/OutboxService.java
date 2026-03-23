package com.los.loanoriginatingsystem.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.outbox.entity.OutboxEvent;
import com.los.loanoriginatingsystem.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void saveEvent(String aggregateType,
                          String aggregateId,
                          String eventType,
                          Object payloadObj) {

        try {

            OutboxEvent event = new OutboxEvent();

            event.setId(UUID.randomUUID().toString());
            event.setAggregateType(aggregateType);
            event.setAggregateId(aggregateId);
            event.setEventType(eventType);
            event.setPayload(objectMapper.writeValueAsString(payloadObj));
            event.setStatus("NEW");
            event.setRetryCount(0);
            event.setCreatedAt(LocalDateTime.now());

            repository.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Outbox serialization failed", e);
        }
    }
}