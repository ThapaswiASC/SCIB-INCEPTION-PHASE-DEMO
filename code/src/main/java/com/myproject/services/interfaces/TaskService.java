package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse getTaskById(Long taskId);
    PagedTaskResponse getUserTasks(Long userId, Pageable pageable);
    TaskCountResponse getTaskCount(Long userId);
    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);
    void deleteTask(Long taskId);
    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
    TaskStatusUpdateResponse updateTaskStatus(String taskId, TaskStatusUpdateRequest request);
    TaskDetailsResponse getTaskDetails(String taskId);
}
