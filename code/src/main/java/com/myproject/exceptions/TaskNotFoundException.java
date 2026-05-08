package com.myproject.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long taskId) {
        super(String.format("Task with ID %d not found", taskId));
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}