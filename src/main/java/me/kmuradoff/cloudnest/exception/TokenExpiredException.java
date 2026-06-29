package me.kmuradoff.cloudnest.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends DomainException {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "EXPIRED_JWT");
    }
}