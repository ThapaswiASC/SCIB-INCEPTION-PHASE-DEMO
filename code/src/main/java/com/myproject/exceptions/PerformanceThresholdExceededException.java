package com.myproject.exceptions;

public class PerformanceThresholdExceededException extends RuntimeException {

    public PerformanceThresholdExceededException(String message) {
        super(message);
    }

    public PerformanceThresholdExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}