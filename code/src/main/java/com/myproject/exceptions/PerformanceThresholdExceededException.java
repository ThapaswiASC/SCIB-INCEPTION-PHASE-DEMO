package com.myproject.exceptions;

public class PerformanceThresholdExceededException extends RuntimeException {
    public PerformanceThresholdExceededException(String message) {
        super(message);
    }
}
