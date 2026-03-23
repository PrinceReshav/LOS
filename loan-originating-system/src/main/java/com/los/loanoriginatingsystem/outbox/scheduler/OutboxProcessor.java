package com.los.loanoriginatingsystem.outbox.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.events.NotificationEvent;
import com.los.loanoriginatingsystem.notification.kafkaProducer.NotificationKafkaProducer;
import com.los.loanoriginatingsystem.outbox.entity.OutboxEvent;
import com.los.loanoriginatingsystem.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxRepository repository;
    private final NotificationKafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void process() {

        List<OutboxEvent> events =
                repository.findTop50ByStatusOrderByCreatedAtAsc("NEW");

        for (OutboxEvent event : events) {

            try {

                NotificationEvent payload =
                        objectMapper.readValue(event.getPayload(), NotificationEvent.class);

                kafkaProducer.send(payload);

                event.setStatus("SENT");
                event.setProcessedAt(LocalDateTime.now());

            } catch (Exception e) {

                log.error("Outbox failed id={}", event.getId(), e);

                event.setRetryCount(event.getRetryCount() + 1);
                event.setStatus("FAILED");
            }

            repository.save(event);
        }
    }
}