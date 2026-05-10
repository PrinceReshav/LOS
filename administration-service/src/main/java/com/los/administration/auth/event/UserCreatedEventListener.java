package com.los.administration.auth.event;

/*

✔ No Kafka here
✔ No saved, role, profile

Because the listener doesn't know about them.

*/


import com.los.events.UserCreatedEvent;
import com.los.administration.auth.service.PasswordTokenService;
import com.los.administration.notification.service.AuthEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedEventListener {

    private final PasswordTokenService passwordTokenService;
    private final AuthEmailService authEmailService;

    @EventListener
    public void onUserCreated(UserCreatedEvent event) {

        String token = passwordTokenService.generateToken(
                event.userId(),
                event.email()
        );

        String setupLink = String.format(
                "https://los.company.com/password/setup?token=%s",
                token
        );

        authEmailService.sendPasswordSetupEmail(
                event.email(),
                event.username(),
                setupLink
        );

       /* log.info(
         *        "PASSWORD_SETUP_INITIATED | userId={} | employeeId={} | email={} | token={} | link={}",
         *       event.userId(),
         *        event.employeeId(),
         *       event.email(),
         *       token,
         *        setupLink
        );*/
        log.info(
                "PASSWORD_SETUP_INITIATED | userId={} | email={}",
                event.userId(),
                event.email()
        );
    }
}