package com.myproject.models.dtos;

import jakarta.validation.constraints.*;

public class UpdateTaskStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(TO_DO|IN_PROGRESS|DONE)$", message = "Status must be TO_DO, IN_PROGRESS, or DONE")
    private String status;

    @NotBlank(message = "Column ID is required")
    private String columnId;

    public UpdateTaskStatusRequest() {}

    public UpdateTaskStatusRequest(String status, String columnId) {
        this.status = status;
        this.columnId = columnId;
    }

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