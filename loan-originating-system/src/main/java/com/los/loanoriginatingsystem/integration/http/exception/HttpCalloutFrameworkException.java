package com.los.loanoriginatingsystem.integration.http.exception;

public class HttpCalloutFrameworkException extends RuntimeException {

    public static final String CUSTOM_METADATA_NOT_FOUND =
            "Unable to load HTTP configuration.";

    public static final String BODY_PARAMETERS_EMPTY =
            "Request body parameters cannot be empty.";

    public HttpCalloutFrameworkException(String message) {
        super(message);
    }
}