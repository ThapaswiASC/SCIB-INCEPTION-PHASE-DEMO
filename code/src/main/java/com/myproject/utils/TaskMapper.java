package com.myproject.utils;

import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.TaskUpdateRequest;
import com.myproject.models.entities.Task;
import com.myproject.models.entities.TaskPriority;
import com.myproject.models.entities.TaskStatus;

public class TaskMapper {

    public static Task toEntity(TaskCreateRequest request) {
        Task task = new Task();
        task.setUserId(request.getUserId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        
        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        } else {
            task.setStatus(TaskStatus.PENDING);
        }
        
        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority()));
        }
        
        return task;
    }

    public static void updateEntity(Task task, TaskUpdateRequest request) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        }
        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority()));
        }
    }

    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setUserId(task.getUserId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        response.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setVersion(task.getVersion());
        return response;
    }
}
