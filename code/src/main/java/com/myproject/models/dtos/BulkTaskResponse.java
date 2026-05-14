package com.myproject.models.dtos;

import java.util.List;

public class BulkTaskResponse {

    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> createdTasks;
    private List<String> errors;

    public BulkTaskResponse() {}

    public BulkTaskResponse(Integer successCount, Integer failureCount, List<TaskResponse> createdTasks, List<String> errors) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.createdTasks = createdTasks;
        this.errors = errors;
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

    public List<TaskResponse> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskResponse> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}