package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse updateTask(Long id, TaskUpdateRequest request);
    TaskResponse getTaskById(Long id);
    List<TaskResponse> getAllTasks();
    void deleteTask(Long id);
    PagedTaskResponse getUserTasks(Long userId, Pageable pageable);
    TaskCountResponse getTaskCount(Long userId);
    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
    UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request);
    TaskDetailsResponse getTaskDetails(String taskId);
}
