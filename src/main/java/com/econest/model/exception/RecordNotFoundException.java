package com.econest.model.exception;

/** Thrown when a product or client cannot be found by its id. */
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
