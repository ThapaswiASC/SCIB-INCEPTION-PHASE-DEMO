package com.myproject.models.dtos;

import java.time.LocalDateTime;

public class UpdateTaskStatusResponse {

    private String taskId;
    private String status;
    private LocalDateTime updatedAt;

    // Constructors
    public UpdateTaskStatusResponse() {}

    public UpdateTaskStatusResponse(String taskId, String status, LocalDateTime updatedAt) {
        this.taskId = taskId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}