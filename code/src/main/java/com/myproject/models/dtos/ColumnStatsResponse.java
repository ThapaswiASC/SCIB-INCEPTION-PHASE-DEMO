package com.myproject.models.dtos;

import java.time.LocalDateTime;

public class ColumnStatsResponse {
    private String columnId;
    private Integer taskCount;
    private LocalDateTime lastUpdated;

    public ColumnStatsResponse() {
    }

    public ColumnStatsResponse(String columnId, Integer taskCount, LocalDateTime lastUpdated) {
        this.columnId = columnId;
        this.taskCount = taskCount;
        this.lastUpdated = lastUpdated;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
