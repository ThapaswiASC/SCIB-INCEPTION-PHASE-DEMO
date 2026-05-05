package com.myproject.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {
    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotBlank(message = "Column ID is required")
    private String columnId;

    public UpdateTaskStatusRequest() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}