package com.myproject.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class BulkTaskResponse {

    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> tasks = new ArrayList<>();
    private List<ErrorResponse> errors = new ArrayList<>();

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
