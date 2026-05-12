package com.myproject.models.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TaskCreateRequest {

    @NotBlank(message = "Task title is required")
    @Size(min = 1, max = 255, message = "Task title must be between 1-255 characters")
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Valid user ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Valid priority level is required")
    private Priority priority;

    private LocalDateTime dueDate;

    public TaskCreateRequest() {
    }

    public TaskCreateRequest(String title, String description, Long userId, Priority priority, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.priority = priority;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}