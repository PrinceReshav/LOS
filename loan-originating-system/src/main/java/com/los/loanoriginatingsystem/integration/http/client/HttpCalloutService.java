package com.los.loanoriginatingsystem.integration.http.client;

import com.los.loanoriginatingsystem.integration.http.config.HttpClientConfig;
import com.los.loanoriginatingsystem.integration.http.exception.HttpCalloutFrameworkException;
import com.los.loanoriginatingsystem.integration.http.multipart.MultipartFormBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpCalloutService {

    private final HttpClientConfig config;
    private final WebClient.Builder webClientBuilder;

    // ✅ UNIVERSAL METHOD (handles JSON + multipart dynamically)
    public String execute(
            String clientName,
            Object body,
            Map<String, String> headers,
            Map<String, byte[]> files) {

        HttpClientConfig.Client client = config.getConfigs().get(clientName);

        if (client == null) {
            throw new HttpCalloutFrameworkException(
                    HttpCalloutFrameworkException.CUSTOM_METADATA_NOT_FOUND
            );
        }

        WebClient webClient = webClientBuilder
                .baseUrl(client.getBaseUrl())
                .build();

        HttpMethod method = HttpMethod.valueOf(
                client.getMethod() != null ? client.getMethod() : "POST"
        );

        MediaType mediaType = resolveContentType(client.getContentType());

        WebClient.RequestBodySpec request = webClient.method(method);

        // headers
        if (headers != null) {
            headers.forEach(request::header);
        }

        // body handling
        if (MediaType.MULTIPART_FORM_DATA.equals(mediaType)) {

            request.contentType(MediaType.MULTIPART_FORM_DATA);

            return request
                    .bodyValue(MultipartFormBuilder.build((Map<String, String>) body, files))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(getTimeout(client)))
                    .block();

        } else {

            request.contentType(MediaType.APPLICATION_JSON);

            return request
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(getTimeout(client)))
                    .block();
        }
    }

    private MediaType resolveContentType(String contentType) {

        if ("multipart/form-data".equalsIgnoreCase(contentType)) {
            return MediaType.MULTIPART_FORM_DATA;
        }

        return MediaType.APPLICATION_JSON;
    }

    private long getTimeout(HttpClientConfig.Client client) {
        return client.getTimeout() != null ? client.getTimeout() : 5000;
    }
}