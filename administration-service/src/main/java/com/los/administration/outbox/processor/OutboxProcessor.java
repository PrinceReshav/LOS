package com.los.administration.outbox.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.administration.kafka.KafkaProducer;
import com.los.administration.outbox.model.OutboxEvent;
import com.los.administration.outbox.model.OutboxStatus;
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

    private static final int MAX_RETRY_COUNT = 10;

    private final OutboxEventRepository outboxRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processOutbox() {

        List<OutboxEvent> events =
                outboxRepository.findByStatus(
                        OutboxStatus.PENDING
                );

        if(events.isEmpty()){
            return;
        }

        for(OutboxEvent event : events){

            try{

                if(!"USER_CREATED".equals(
                        event.getEventType()
                )){
                    continue;
                }

                UserCreatedEvent payload =
                        objectMapper.readValue(
                                event.getPayload(),
                                UserCreatedEvent.class
                        );

                kafkaProducer
                        .publishUserCreated(payload)
                        .get();

                event.setStatus(
                        OutboxStatus.KAFKA_PUBLISHED
                );

                log.info(
                        "OUTBOX_EVENT_PUBLISHED id={} aggregateId={}",
                        event.getId(),
                        event.getAggregateId()
                );

            } catch (Exception ex){

                event.setRetryCount(
                        event.getRetryCount() + 1
                );

                if(event.getRetryCount()
                        >= MAX_RETRY_COUNT){

                    event.setStatus(
                            OutboxStatus.FAILED
                    );
                }

                log.error(
                        "OUTBOX_EVENT_FAILED id={} retry={} reason={}",
                        event.getId(),
                        event.getRetryCount(),
                        ex.getMessage()
                );
            }
        }
    }
}