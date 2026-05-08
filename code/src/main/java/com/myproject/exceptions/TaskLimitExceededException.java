package com.myproject.exceptions;

public class TaskLimitExceededException extends RuntimeException {

    public TaskLimitExceededException(String message) {
        super(message);
    }

    public TaskLimitExceededException(Long userId, int maxTasks) {
        super(String.format("User %d has exceeded the maximum task limit of %d", userId, maxTasks));
    }
}