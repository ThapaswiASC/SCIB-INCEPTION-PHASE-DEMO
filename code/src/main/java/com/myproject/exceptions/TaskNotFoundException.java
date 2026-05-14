package com.myproject.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(Long taskId) {
        super("Task not found with ID: " + taskId);
    }
}