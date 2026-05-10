package com.los.administration.kafka;

import com.los.administration.notification.idempotency.IdempotencyRecord;
import com.los.administration.notification.idempotency.IdempotencyRepository;
import com.los.events.NotificationEvent;
import com.los.administration.notification.dto.NotificationRequest;
import com.los.administration.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final IdempotencyRepository idempotencyRepository;


    @Transactional
    @KafkaListener(
            topics = "los.notification",
            groupId = "notification-group"
    )
    public void consume(NotificationEvent event) {

        if (idempotencyRepository.existsById(event.eventId())) {
            log.warn("Duplicate event skipped {}", event.eventId());
            return;
        }

        NotificationRequest req = new NotificationRequest();
        req.setTemplateCode(event.templateCode());
        req.setRecipients(event.recipients());
        req.setMetadata(event.metadata());

        notificationService.send(req);

        IdempotencyRecord record = new IdempotencyRecord();
        record.setEventId(event.eventId());
        record.setProcessedAt(LocalDateTime.now());

        idempotencyRepository.save(record);
    }
}