package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse getTaskById(Long taskId);
    PagedTaskResponse getUserTasks(Long userId, int page, int size);
    TaskCountResponse getTaskCount(Long userId);
    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);
    void deleteTask(Long taskId);
    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
    UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request);
    TaskDetailsResponse getTaskDetails(String taskId);
}
