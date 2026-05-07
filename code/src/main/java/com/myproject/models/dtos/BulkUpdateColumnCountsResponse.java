package com.myproject.models.dtos;

import java.util.List;

public class BulkUpdateColumnCountsResponse {

    private Boolean success;
    private List<String> updatedColumns;

    // Constructors
    public BulkUpdateColumnCountsResponse() {}

    public BulkUpdateColumnCountsResponse(Boolean success, List<String> updatedColumns) {
        this.success = success;
        this.updatedColumns = updatedColumns;
    }

    // Getters and Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<String> getUpdatedColumns() {
        return updatedColumns;
    }

    public void setUpdatedColumns(List<String> updatedColumns) {
        this.updatedColumns = updatedColumns;
    }
}