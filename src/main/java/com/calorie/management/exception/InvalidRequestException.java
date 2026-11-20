package com.calorie.management.exception;

/** Business-rule validation failure. Mapped to 400. */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
