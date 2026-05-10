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

        if (event == null || event.eventId() == null) {
            log.warn("Skipping Kafka send due to invalid event");
            return;
        }

        try {
            kafkaTemplate.send(TOPIC, event.eventId(), event)
                    .whenComplete((result, ex) -> {

                        if (ex != null) {
                            log.error("Kafka publish failed eventId={}", event.eventId(), ex);
                        } else {
                            log.info("Kafka success eventId={} topic={} partition={} offset={}",
                                    event.eventId(),
                                    TOPIC,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

        } catch (Exception e) {
            log.error("Kafka send failed completely eventId={}", event.eventId(), e);
        }
    }
}