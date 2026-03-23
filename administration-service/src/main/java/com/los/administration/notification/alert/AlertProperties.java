package com.los.administration.notification.alert;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "alerts")
public class AlertProperties {

    private Email email = new Email();
    private Slack slack = new Slack();

    @Data
    public static class Email {
        private boolean enabled;
        private String recipients;

        public List<String> getRecipientsList() {
            if (recipients == null) return List.of();
            return Arrays.asList(recipients.split(","));
        }
    }

    @Data
    public static class Slack {
        private boolean enabled;
        private String webhookUrl;
    }
}