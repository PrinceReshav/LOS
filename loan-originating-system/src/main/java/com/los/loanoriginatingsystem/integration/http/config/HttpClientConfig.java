package com.los.loanoriginatingsystem.integration.http.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "http.clients")
public class HttpClientConfig {

    private Map<String, Client> configs;

    @Data
    public static class Client {
        private String baseUrl;
        private String method;
        private String contentType;
        private Integer timeout;
    }
}