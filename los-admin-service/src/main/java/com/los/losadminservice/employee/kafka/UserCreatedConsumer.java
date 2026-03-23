package com.los.losadminservice.employee.kafka;

import com.los.events.UserCreatedEvent;
import com.los.losadminservice.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(
            topics = "los.user.created",
            groupId = "los-admin-service"
    )
    public void consume(UserCreatedEvent event){

        log.info(
                "USER_CREATED_EVENT_RECEIVED userId={} employeeId={}",
                event.userId(),
                event.employeeId()
        );

        employeeService.createEmployeeFromUserEvent(event);
    }
}