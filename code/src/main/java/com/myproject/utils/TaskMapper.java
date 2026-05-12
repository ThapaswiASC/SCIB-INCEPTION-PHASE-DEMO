package com.myproject.utils;

import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.entities.Task;

public class TaskMapper {

    private TaskMapper() {
        // Utility class
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setUserId(task.getUserId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setDueDate(task.getDueDate());
        response.setVersion(task.getVersion());

        return response;
    }
}