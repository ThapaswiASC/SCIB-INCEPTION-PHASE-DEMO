package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskCounterDataStore;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.models.entities.TaskCounter;
import com.myproject.services.interfaces.PerformanceMonitoringService;
import com.myproject.services.interfaces.TaskService;
import com.myproject.utils.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    @Value("${app.task.max-per-user:10000}")
    private int maxTasksPerUser;

    @Value("${app.performance.threshold-ms:200}")
    private long performanceThresholdMs;

    @Autowired
    private TaskDataStore taskDataStore;

    @Autowired
    private TaskCounterDataStore taskCounterDataStore;

    @Autowired
    private PerformanceMonitoringService performanceMonitoringService;

    @Override
    public TaskResponse createTask(String userId, TaskCreateRequest request) {
        long startTime = System.currentTimeMillis();

        // Validate task limit
        validateTaskLimit(userId);

        // Create task
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskDataStore.save(task);

        // Update task counter
        taskCounterDataStore.incrementTaskCount(userId);

        // Monitor performance
        long executionTime = System.currentTimeMillis() - startTime;
        performanceMonitoringService.recordTaskCreationTime(executionTime);

        if (executionTime > performanceThresholdMs) {
            throw new PerformanceThresholdExceededException(
                    "Task creation exceeded performance threshold: " + executionTime + "ms");
        }

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(String userId, List<TaskCreateRequest> requests) {
        BulkTaskResponse response = new BulkTaskResponse();
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<BulkTaskResponse.BulkTaskError> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskResponse taskResponse = createTask(userId, requests.get(i));
                createdTasks.add(taskResponse);
            } catch (Exception e) {
                BulkTaskResponse.BulkTaskError error = new BulkTaskResponse.BulkTaskError();
                error.setIndex(i);
                error.setErrorCode("TASK_CREATION_FAILED");
                error.setMessage(e.getMessage());
                errors.add(error);
            }
        }

        response.setTotalCreated(createdTasks.size());
        response.setTasks(createdTasks);
        response.setErrors(errors);

        return response;
    }

    @Override
    public TaskResponse getTaskById(UUID taskId, String userId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedException("Access denied to task: " + taskId);
        }

        return TaskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, String userId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedException("Access denied to task: " + taskId);
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskDataStore.save(task);

        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(UUID taskId, String userId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedException("Access denied to task: " + taskId);
        }

        taskDataStore.deleteById(taskId);
        taskCounterDataStore.decrementTaskCount(userId);
    }

    @Override
    public List<TaskResponse> getUserTasks(String userId, int page, int size, String sort) {
        List<Task> tasks = taskDataStore.findByUserId(userId, page, size, sort);
        return tasks.stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskCountResponse getTaskCount(String userId) {
        long taskCount = taskDataStore.countByUserId(userId);

        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(taskCount);
        response.setMaxTasksAllowed(maxTasksPerUser);
        response.setRemainingCapacity((int) (maxTasksPerUser - taskCount));

        return response;
    }

    private void validateTaskLimit(String userId) {
        long currentTaskCount = taskDataStore.countByUserId(userId);
        if (currentTaskCount >= maxTasksPerUser) {
            throw new TaskLimitExceededException(
                    "User has reached maximum task limit of " + maxTasksPerUser);
        }
    }
}