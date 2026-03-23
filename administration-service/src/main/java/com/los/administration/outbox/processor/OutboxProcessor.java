package com.los.administration.outbox.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.administration.kafka.KafkaProducer;
import com.los.administration.outbox.model.OutboxEvent;
import com.los.administration.outbox.repository.OutboxEventRepository;
import com.los.events.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processOutbox() {

        List<OutboxEvent> events =
                outboxRepository.findTop50ByPublishedFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        for (OutboxEvent event : events) {

            try {

                if ("USER_CREATED".equals(event.getEventType())) {

                    UserCreatedEvent payload =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    UserCreatedEvent.class
                            );

                    kafkaProducer.publishUserCreated(payload);
                }
                event.setPublished(true);
                log.info(
                        "OUTBOX_EVENT_PUBLISHED id={} aggregateId={}",
                        event.getId(),
                        event.getAggregateId()
                );
            } catch (Exception ex) {
                log.error(
                        "OUTBOX_EVENT_FAILED id={} reason={}",
                        event.getId(),
                        ex.getMessage()
                );
            }
        }
    }
}