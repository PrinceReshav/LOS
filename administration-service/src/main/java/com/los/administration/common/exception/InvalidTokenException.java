package com.los.administration.common.exception;

public class InvalidTokenException extends BadRequestException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
