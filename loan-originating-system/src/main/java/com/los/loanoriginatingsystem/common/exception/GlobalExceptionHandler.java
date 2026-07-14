package com.los.loanoriginatingsystem.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>>
    handleRuntimeException(
            RuntimeException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }
}