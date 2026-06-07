package com.los.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {

        return exchange -> {

            String forwardedIp =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst("X-Forwarded-For");

            if (forwardedIp != null) {
                return Mono.just(forwardedIp);
            }

            return Mono.just(
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
            );
        };
    }
}