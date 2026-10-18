package com.econest.model.exception;

/** Wraps SQLException so upper layers never depend on java.sql. */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
