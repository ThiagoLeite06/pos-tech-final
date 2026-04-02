package br.com.susreceita.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain exception for authentication and authorisation failures.
 * Carries the HTTP status so the global handler can map it without coupling to Spring MVC.
 */
public class AuthException extends RuntimeException {

    private final HttpStatus status;

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AuthException unauthorized(String detail) {
        return new AuthException(detail, HttpStatus.UNAUTHORIZED);
    }
}
