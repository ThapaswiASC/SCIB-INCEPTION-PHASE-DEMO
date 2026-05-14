package com.myproject.models.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TaskCreateRequest {

    @NotBlank(message = "Title is required and cannot be empty or contain only whitespace")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|URGENT)$", message = "Priority must be LOW, MEDIUM, HIGH, or URGENT")
    private String priority;

    private LocalDateTime dueDate;

    public TaskCreateRequest() {}

    public TaskCreateRequest(String title, String description, Long userId, String priority, LocalDateTime dueDate) {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}