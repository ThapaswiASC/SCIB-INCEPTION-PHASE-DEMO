package com.myproject.models.dtos;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TaskUpdateRequest {

    @Size(min = 1, max = 255, message = "Task title must be between 1-255 characters")
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    private String description;

    private Priority priority;

    private TaskStatus status;

    private LocalDateTime dueDate;

    public TaskUpdateRequest() {
    }

    public TaskUpdateRequest(String title, String description, Priority priority, TaskStatus status, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}