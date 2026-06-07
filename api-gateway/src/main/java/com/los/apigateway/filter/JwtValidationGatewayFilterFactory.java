package com.los.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<
        JwtValidationGatewayFilterFactory.Config> {

    private final WebClient.Builder webClientBuilder;
    private final String authServiceUrl;


    public JwtValidationGatewayFilterFactory(
            WebClient.Builder builder,
            @Value("${auth.service.url}") String authServiceUrl
    ) {
        super(Config.class);

        this.webClientBuilder = builder;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public GatewayFilter apply(Config config) {



        return (exchange, chain) -> {

            System.out.println(
                    "GATEWAY REQUEST: " +
                            exchange.getRequest().getMethod() +
                            " " +
                            exchange.getRequest().getURI()
            );

            String token =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(HttpHeaders.AUTHORIZATION);


            if (exchange.getRequest()
                    .getMethod()
                    .name()
                    .equals("OPTIONS")) {

                return chain.filter(exchange);
            }

            if (token == null ||
                    !token.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse()
                        .setComplete();
            }

            return webClientBuilder
                    .build()
                    .get()
                    .uri(authServiceUrl + "/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange));
        };
    }

    public static class Config {
    }
}