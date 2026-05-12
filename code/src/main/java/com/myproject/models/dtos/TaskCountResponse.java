package com.myproject.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class TaskCountResponse {

    private Long userId;

    @Min(0)
    @Max(10000)
    private Integer taskCount;

    public TaskCountResponse() {
    }

    public TaskCountResponse(Long userId, Integer taskCount) {
        this.userId = userId;
        this.taskCount = taskCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }
}