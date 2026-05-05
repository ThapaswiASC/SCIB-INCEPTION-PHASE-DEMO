package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;
import java.util.List;

public interface TaskService {
    UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request);
    TaskDetailsResponse getTaskDetails(String taskId);
    TaskResponse createTask(TaskCreateRequest request);
    PagedTaskResponse getUserTasks(Long userId, int page, int size, String sort);
    TaskCountResponse getTaskCount(Long userId);
    TaskResponse getTaskById(Long taskId);
    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);
    void deleteTask(Long taskId);
    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
}