package com.myproject.models.dtos;

import jakarta.validation.constraints.*;

public class TaskCreateRequestValidated {

    @NotNull(message = "Title is required")
    @NotBlank(message = "Title cannot be empty or contain only whitespace")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Title cannot be empty or contain only whitespace")
    private String title;

    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private String priority;

    public TaskCreateRequestValidated() {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}