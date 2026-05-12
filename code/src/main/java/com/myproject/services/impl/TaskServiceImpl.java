package com.myproject.services.impl;

import com.myproject.exceptions.PerformanceThresholdExceededException;
import com.myproject.exceptions.ResourceNotFoundException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.datastores.UserTaskCountCache;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import com.myproject.utils.PerformanceMonitor;
import com.myproject.utils.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Value("${app.task.max-per-user:10000}")
    private int maxTasksPerUser;

    @Value("${app.performance.threshold-ms:200}")
    private long performanceThresholdMs;

    @Autowired
    private TaskDataStore taskDataStore;

    @Autowired
    private UserTaskCountCache userTaskCountCache;

    @Autowired
    private PerformanceMonitor performanceMonitor;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        long startTime = System.currentTimeMillis();
        
        logger.debug("Creating task for user: {}", request.getUserId());
        
        // Check task limit
        validateTaskLimit(request.getUserId());
        
        // Create task
        Task task = TaskMapper.toEntity(request);
        task.setCreatedAt(LocalDateTime.now());
        
        Task savedTask = taskDataStore.save(task);
        
        // Update cache
        userTaskCountCache.incrementCount(request.getUserId());
        
        // Monitor performance
        long executionTime = System.currentTimeMillis() - startTime;
        performanceMonitor.recordTaskCreation(executionTime);
        
        if (executionTime > performanceThresholdMs) {
            throw new PerformanceThresholdExceededException(
                "Task creation exceeded performance threshold: " + executionTime + "ms");
        }
        
        logger.info("Task created successfully with ID: {} in {}ms", savedTask.getId(), executionTime);
        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getUserTasks(Long userId, int page, int size) {
        logger.debug("Fetching tasks for user: {}, page: {}, size: {}", userId, page, size);
        
        List<Task> tasks = taskDataStore.findByUserId(userId, page, size);
        return tasks.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        logger.debug("Getting task count for user: {}", userId);
        
        long count = taskDataStore.countByUserId(userId);
        
        // Sync cache with actual count
        userTaskCountCache.setCount(userId, count);
        
        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(count);
        response.setMaxTasksAllowed(maxTasksPerUser);
        
        return response;
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        logger.debug("Fetching task by ID: {}", taskId);
        
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        return TaskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        logger.debug("Updating task with ID: {}", taskId);
        
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        // Check version for optimistic locking
        if (request.getVersion() != null && !request.getVersion().equals(task.getVersion())) {
            throw new RuntimeException("Optimistic locking failure - task was modified by another user");
        }
        
        TaskMapper.updateEntity(task, request);
        task.setUpdatedAt(LocalDateTime.now());
        
        Task updatedTask = taskDataStore.save(task);
        
        logger.info("Task updated successfully with ID: {}", taskId);
        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        logger.debug("Deleting task with ID: {}", taskId);
        
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        taskDataStore.deleteById(taskId);
        userTaskCountCache.decrementCount(task.getUserId());
        
        logger.info("Task deleted successfully with ID: {}", taskId);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        logger.debug("Bulk creating {} tasks", requests.size());
        
        BulkTaskResponse response = new BulkTaskResponse();
        List<TaskResponse> successfulTasks = new ArrayList<>();
        List<ErrorResponse> errors = new ArrayList<>();
        
        for (TaskCreateRequest request : requests) {
            try {
                TaskResponse taskResponse = createTask(request);
                successfulTasks.add(taskResponse);
            } catch (Exception e) {
                ErrorResponse error = new ErrorResponse();
                error.setErrorCode("TASK_CREATION_FAILED");
                error.setMessage(e.getMessage());
                error.setTimestamp(System.currentTimeMillis());
                errors.add(error);
            }
        }
        
        response.setSuccessCount(successfulTasks.size());
        response.setFailureCount(errors.size());
        response.setTasks(successfulTasks);
        response.setErrors(errors);
        
        logger.info("Bulk task creation completed: {} successful, {} failed", 
                successfulTasks.size(), errors.size());
        
        return response;
    }

    private void validateTaskLimit(Long userId) {
        long currentCount = userTaskCountCache.getCount(userId);
        
        // Double-check with actual count if cache seems off
        if (currentCount >= maxTasksPerUser - 100) {
            currentCount = taskDataStore.countByUserId(userId);
            userTaskCountCache.setCount(userId, currentCount);
        }
        
        if (currentCount >= maxTasksPerUser) {
            throw new TaskLimitExceededException(
                "User has reached maximum task limit of " + maxTasksPerUser);
        }
    }
}
