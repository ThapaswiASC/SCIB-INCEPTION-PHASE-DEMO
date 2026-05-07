package com.myproject.services.impl;

import com.myproject.exceptions.DatabaseConnectionException;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.exceptions.TimeoutException;
import com.myproject.exceptions.ValidationException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskDataStore taskDataStore;

    @Override
    @Retryable(
        retryFor = {DatabaseConnectionException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public TaskResponse createTask(TaskCreateRequest request) {
        try {
            logger.info("Creating task with title: {}", request.getTitle());
            
            // Validate input
            validateTaskRequest(request);
            
            // Create task entity
            Task task = mapToEntity(request);
            
            // Save task
            Task savedTask = saveWithRetry(task, request);
            
            logger.info("Task created successfully with id: {}", savedTask.getId());
            return mapToResponse(savedTask);
            
        } catch (DatabaseConnectionException | TimeoutException ex) {
            logger.error("Error creating task: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during task creation", ex);
            throw new DatabaseConnectionException("Unexpected error during task creation", request, ex);
        }
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        logger.info("Fetching task with id: {}", id);
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return mapToResponse(task);
    }

    @Override
    @Retryable(
        retryFor = {DatabaseConnectionException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        try {
            logger.info("Updating task with id: {}", id);
            
            Task existingTask = taskDataStore.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException(id));
            
            // Update fields if provided
            if (request.getTitle() != null) {
                existingTask.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                existingTask.setDescription(request.getDescription());
            }
            if (request.getPriority() != null) {
                existingTask.setPriority(request.getPriority());
            }
            if (request.getStatus() != null) {
                existingTask.setStatus(request.getStatus());
            }
            if (request.getDueDate() != null) {
                existingTask.setDueDate(request.getDueDate());
            }
            
            existingTask.setUpdatedAt(LocalDateTime.now());
            
            Task updatedTask = taskDataStore.save(existingTask);
            logger.info("Task updated successfully with id: {}", updatedTask.getId());
            return mapToResponse(updatedTask);
            
        } catch (TaskNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating task", ex);
            throw new DatabaseConnectionException("Error updating task", request, ex);
        }
    }

    @Override
    public void deleteTask(Long id) {
        logger.info("Deleting task with id: {}", id);
        if (!taskDataStore.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskDataStore.deleteById(id);
        logger.info("Task deleted successfully with id: {}", id);
    }

    private void validateTaskRequest(TaskCreateRequest request) {
        List<String> errors = new ArrayList<>();
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (request.getTitle().length() > 100) {
            errors.add("Title must not exceed 100 characters");
        }
        
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            errors.add("Description must not exceed 500 characters");
        }
        
        if (request.getPriority() == null) {
            errors.add("Priority is required");
        }
        
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            errors.add("Due date must be in the future");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    private Task saveWithRetry(Task task, TaskCreateRequest originalRequest) {
        try {
            return taskDataStore.save(task);
        } catch (Exception ex) {
            logger.error("Error saving task to datastore", ex);
            throw new DatabaseConnectionException("Database operation failed", originalRequest, ex);
        }
    }

    private Task mapToEntity(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        task.setUserId(1L); // Default user ID for demo
        return task;
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDueDate(),
                task.getUserId()
        );
    }
}