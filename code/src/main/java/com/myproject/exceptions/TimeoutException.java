package com.myproject.exceptions;

public class TimeoutException extends RuntimeException {
    private final Object preservedInput;

    public TimeoutException(String message, Object preservedInput) {
        super(message);
        this.preservedInput = preservedInput;
    }

    public TimeoutException(String message, Object preservedInput, Throwable cause) {
        super(message, cause);
        this.preservedInput = preservedInput;
    }

    public Object getPreservedInput() {
        return preservedInput;
    }
}