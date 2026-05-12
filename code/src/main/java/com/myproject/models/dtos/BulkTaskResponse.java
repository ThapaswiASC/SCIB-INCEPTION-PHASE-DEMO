package com.myproject.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class BulkTaskResponse {

    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> tasks;
    private List<ErrorResponse> errors;

    public BulkTaskResponse() {
        this.tasks = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public BulkTaskResponse(Integer successCount, Integer failureCount, List<TaskResponse> tasks, List<ErrorResponse> errors) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.errors = errors != null ? errors : new ArrayList<>();
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

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}