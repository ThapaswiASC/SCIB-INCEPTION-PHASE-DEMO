package com.myproject.exceptions;

public class DatabaseConnectionException extends RuntimeException {
    private final Object preservedInput;

    public DatabaseConnectionException(String message, Object preservedInput) {
        super(message);
        this.preservedInput = preservedInput;
    }

    public DatabaseConnectionException(String message, Object preservedInput, Throwable cause) {
        super(message, cause);
        this.preservedInput = preservedInput;
    }

    public Object getPreservedInput() {
        return preservedInput;
    }
}