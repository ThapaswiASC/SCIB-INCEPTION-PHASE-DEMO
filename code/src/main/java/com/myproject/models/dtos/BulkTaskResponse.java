package com.myproject.models.dtos;

import java.util.List;

public class BulkTaskResponse {
    private List<TaskResponse> createdTasks;
    private Integer successCount;
    private Integer failureCount;
    private List<String> errors;

    public BulkTaskResponse() {
    }

    public BulkTaskResponse(List<TaskResponse> createdTasks, Integer successCount, Integer failureCount, List<String> errors) {
        this.createdTasks = createdTasks;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors;
    }

    public List<TaskResponse> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskResponse> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
