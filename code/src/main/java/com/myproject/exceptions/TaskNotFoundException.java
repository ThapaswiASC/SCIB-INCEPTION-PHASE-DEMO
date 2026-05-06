package com.myproject.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long taskId) {
        super("Task not found with ID: " + taskId);
    }
    
    public TaskNotFoundException(String taskId) {
        super("Task not found with ID: " + taskId);
    }
}