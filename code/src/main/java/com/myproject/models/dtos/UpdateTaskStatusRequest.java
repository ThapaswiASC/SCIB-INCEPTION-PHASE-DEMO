package com.myproject.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private String status;

    @NotBlank(message = "Column ID is required")
    private String columnId;

    // Constructors
    public UpdateTaskStatusRequest() {}

    public UpdateTaskStatusRequest(String status, String columnId) {
        this.status = status;
        this.columnId = columnId;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}