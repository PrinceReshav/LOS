package com.los.loanoriginatingsystem.notification.kafkaProducer;


import com.los.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private static final String TOPIC = "los.notification";

    public void send(NotificationEvent event) {

        kafkaTemplate.send(TOPIC, event.eventId(), event)
                .whenComplete((res, ex) -> {

                    if (ex != null) {
                        log.error("Kafka publish failed eventId={}", event.eventId(), ex);
                    } else {
                        log.info("Kafka published eventId={} partition={}",
                                event.eventId(),
                                res.getRecordMetadata().partition());
                    }
                });
    }
}