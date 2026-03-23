    package com.los.loanoriginatingsystem.integration.logging.service;

    import com.los.loanoriginatingsystem.integration.logging.entity.ApiLog;
    import com.los.loanoriginatingsystem.integration.logging.repository.ApiLogRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class ApiLogService {

        private final ApiLogRepository apiLogRepository;

        public void log(
                String integrationType,
                String requestBody,
                String responseBody,
                String applicationId) {

            ApiLog log = new ApiLog();

            log.setIntegrationType(integrationType);
            log.setRequestBody(truncate(requestBody));
            log.setResponseBody(truncate(responseBody));
            log.setApplicationId(applicationId);

            apiLogRepository.save(log);
        }

        private String truncate(String text) {
            if (text == null) return null;

            int maxLength = 131072;

            return text.length() > maxLength
                    ? text.substring(0, maxLength)
                    : text;
        }
    }