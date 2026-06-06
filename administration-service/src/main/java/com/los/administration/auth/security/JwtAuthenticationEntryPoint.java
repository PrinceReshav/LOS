package com.los.administration.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.administration.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   // private final ObjectMapper objectMapper = new ObjectMapper();
    //InvalidDefinitionException:
   //Java 8 date/time type java.time.LocalDateTime not supported

    private final ObjectMapper objectMapper;


    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ApiResponse<?> apiResponse =
                ApiResponse.error("UNAUTHORIZED");

        response.getWriter()
                .write(objectMapper.writeValueAsString(apiResponse));
    }
}
