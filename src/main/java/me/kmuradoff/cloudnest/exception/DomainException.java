package me.kmuradoff.cloudnest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public DomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = null;
    }

    public DomainException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }
}