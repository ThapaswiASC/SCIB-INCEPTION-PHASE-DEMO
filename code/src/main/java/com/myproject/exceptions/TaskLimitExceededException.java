package com.myproject.exceptions;

public class TaskLimitExceededException extends RuntimeException {
    public TaskLimitExceededException(String message) {
        super(message);
    }
    
    public TaskLimitExceededException(Long userId, long currentCount, long maxLimit) {
        super(String.format("User %d has reached task limit. Current: %d, Max: %d", userId, currentCount, maxLimit));
    }
}