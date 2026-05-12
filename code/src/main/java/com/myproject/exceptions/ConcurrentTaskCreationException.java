package com.myproject.exceptions;

public class ConcurrentTaskCreationException extends RuntimeException {

    public ConcurrentTaskCreationException(String message) {
        super(message);
    }

    public ConcurrentTaskCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}