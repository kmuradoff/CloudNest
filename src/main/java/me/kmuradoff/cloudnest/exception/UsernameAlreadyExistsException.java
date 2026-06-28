package me.kmuradoff.cloudnest.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends DomainException {
    public UsernameAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS");
    }
}