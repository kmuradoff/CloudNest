package me.kmuradoff.cloudnest.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
    }
}