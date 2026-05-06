package com.myproject.exceptions;

public class TaskLimitExceededException extends RuntimeException {
    public TaskLimitExceededException(String message) {
        super(message);
    }

    public TaskLimitExceededException(Long userId, int currentCount, int maxLimit) {
        super(String.format("User %d has reached task limit: %d/%d", userId, currentCount, maxLimit));
    }
}