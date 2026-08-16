package com.los.loanoriginatingsystem.integration.http.client;

import com.los.loanoriginatingsystem.integration.config.entity.AuthType;
import com.los.loanoriginatingsystem.integration.config.entity.IntegrationConfig;
import com.los.loanoriginatingsystem.integration.config.service.IntegrationConfigService;
import com.los.loanoriginatingsystem.integration.http.config.HttpClientConfig;
import com.los.loanoriginatingsystem.integration.http.exception.HttpCalloutFrameworkException;
import com.los.loanoriginatingsystem.integration.http.multipart.MultipartFormBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * Generic outbound-integration HTTP client.
 *
 * Resolution order for a given clientName:
 *   1. DB-backed IntegrationConfig (admin-editable, see integration.config
 *      package) - if an ACTIVE row exists for this clientName, it wins.
 *   2. Legacy {@code http.clients.*} properties (HttpClientConfig) - kept
 *      as a fallback so existing property-configured clients keep working
 *      without needing a DB row seeded first.
 *
 * When resolved from the DB, the configured AuthType is used to attach the
 * decrypted credential to the request (header / bearer / basic). Property-
 * based clients carry no credential - callers must still pass any
 * necessary auth header explicitly via the `headers` map, same as before.
 */
@Service
@RequiredArgsConstructor
public class HttpCalloutService {

    private final HttpClientConfig config;
    private final IntegrationConfigService integrationConfigService;
    private final WebClient.Builder webClientBuilder;

    // ✅ UNIVERSAL METHOD (handles JSON + multipart dynamically)
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String execute(
            String clientName,
            Object body,
            Map<String, String> headers,
            Map<String, byte[]> files) {

        ResolvedClient client = resolve(clientName);

        WebClient webClient = webClientBuilder
                .baseUrl(client.baseUrl())
                .build();

        HttpMethod method = HttpMethod.valueOf(client.method());
        MediaType mediaType = resolveContentType(client.contentType());

        WebClient.RequestBodySpec request = webClient.method(method);

        // static/config-driven headers first (auth, etc), then caller-supplied
        // headers, which take priority so a caller can always override.
        client.staticAuthHeaders().forEach(request::header);
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
                    .timeout(Duration.ofMillis(client.timeoutMs()))
                    .block();

        } else {

            request.contentType(MediaType.APPLICATION_JSON);

            return request
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(client.timeoutMs()))
                    .block();
        }
    }

    // ==========================================================
    // Resolution: DB config (preferred) -> properties fallback
    // ==========================================================

    private ResolvedClient resolve(String clientName) {

        IntegrationConfig dbConfig = integrationConfigService.getActiveForRuntime(clientName);

        if (dbConfig != null) {
            return fromDbConfig(dbConfig);
        }

        HttpClientConfig.Client propsClient =
                config.getConfigs() != null ? config.getConfigs().get(clientName) : null;

        if (propsClient == null) {
            throw new HttpCalloutFrameworkException(
                    HttpCalloutFrameworkException.CUSTOM_METADATA_NOT_FOUND
            );
        }

        return new ResolvedClient(
                propsClient.getBaseUrl(),
                propsClient.getMethod() != null ? propsClient.getMethod() : "POST",
                propsClient.getContentType(),
                propsClient.getTimeout() != null ? propsClient.getTimeout() : 5000,
                Map.of()
        );
    }

    private ResolvedClient fromDbConfig(IntegrationConfig cfg) {

        Map<String, String> authHeaders = new java.util.HashMap<>();

        if (cfg.getStaticHeaders() != null && !cfg.getStaticHeaders().isBlank()) {
            for (String pair : cfg.getStaticHeaders().split(";")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    authHeaders.put(kv[0].trim(), kv[1].trim());
                }
            }
        }

        AuthType authType = cfg.getAuthType() != null ? cfg.getAuthType() : AuthType.NONE;

        switch (authType) {
            case API_KEY_HEADER -> {
                String headerName = cfg.getAuthHeaderName() != null ? cfg.getAuthHeaderName() : "x-api-key";
                authHeaders.put(headerName, integrationConfigService.decryptApiKey(cfg));
            }
            case BEARER_TOKEN -> authHeaders.put(
                    HttpHeaders.AUTHORIZATION, "Bearer " + integrationConfigService.decryptApiKey(cfg)
            );
            case BASIC_AUTH -> {
                String user = integrationConfigService.decryptApiKey(cfg);
                String pass = integrationConfigService.decryptApiSecret(cfg);
                String basic = Base64.getEncoder().encodeToString(
                        (user + ":" + pass).getBytes(StandardCharsets.UTF_8)
                );
                authHeaders.put(HttpHeaders.AUTHORIZATION, "Basic " + basic);
            }
            case NONE -> { /* no-op */ }
        }

        return new ResolvedClient(
                cfg.getBaseUrl(),
                cfg.getHttpMethod() != null ? cfg.getHttpMethod() : "POST",
                cfg.getContentType(),
                cfg.getTimeoutMs() != null ? cfg.getTimeoutMs() : 10000,
                authHeaders
        );
    }

    private MediaType resolveContentType(String contentType) {

        if ("multipart/form-data".equalsIgnoreCase(contentType)) {
            return MediaType.MULTIPART_FORM_DATA;
        }

        return MediaType.APPLICATION_JSON;
    }

    private record ResolvedClient(
            String baseUrl,
            String method,
            String contentType,
            long timeoutMs,
            Map<String, String> staticAuthHeaders
    ) {}
}