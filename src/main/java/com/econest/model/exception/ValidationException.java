package com.econest.model.exception;

/** Thrown when user input or a domain rule is violated (empty name, bad email, price <= 0...). */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
