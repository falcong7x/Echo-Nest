package com.econest.model.exception;

/** Thrown when a record that must be unique (product name, client email) already exists. */
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
