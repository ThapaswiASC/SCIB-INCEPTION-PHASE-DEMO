package com.myproject.models.dtos;

public class TaskDetailsResponse {
    private String taskId;
    private String title;
    private String status;
    private String columnId;

    public TaskDetailsResponse() {
    }

    public TaskDetailsResponse(String taskId, String title, String status, String columnId) {
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.columnId = columnId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
