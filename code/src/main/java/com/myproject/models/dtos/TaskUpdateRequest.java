package com.myproject.models.dtos;

import jakarta.validation.constraints.*;

public class TaskUpdateRequest {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Priority must be LOW, MEDIUM, or HIGH")
    private String priority;

    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE)$", message = "Status must be TODO, IN_PROGRESS, or DONE")
    private String status;

    public TaskUpdateRequest() {}

    public TaskUpdateRequest(String title, String description, String priority, String status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}