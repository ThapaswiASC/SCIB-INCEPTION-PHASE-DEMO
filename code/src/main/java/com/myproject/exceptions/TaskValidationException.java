package com.myproject.exceptions;

public class TaskValidationException extends RuntimeException {
    private final String field;
    private final String rejectedValue;

    public TaskValidationException(String field, String rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public TaskValidationException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}