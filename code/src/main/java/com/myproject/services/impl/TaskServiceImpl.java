package com.myproject.services.impl;

import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import com.myproject.utils.TaskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskDataStore taskDataStore;
    private final TaskValidator taskValidator;

    @Autowired
    public TaskServiceImpl(TaskDataStore taskDataStore, TaskValidator taskValidator) {
        this.taskDataStore = taskDataStore;
        this.taskValidator = taskValidator;
    }

    @Override
    @Retryable(maxAttempts = 3)
    public TaskResponse createTask(TaskCreateRequest request) {
        logger.debug("Creating task with title: {}", request.getTitle());
        
        // Validate input
        taskValidator.validateCreateRequest(request);
        
        // Create task entity
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        
        // Save to datastore
        Task savedTask = taskDataStore.save(task);
        
        logger.info("Task created successfully with id: {}", savedTask.getId());
        return TaskResponse.from(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        logger.debug("Fetching task with id: {}", id);
        
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        return TaskResponse.from(task);
    }

    @Override
    @Retryable(maxAttempts = 3)
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        logger.debug("Updating task with id: {}", id);
        
        // Validate input
        taskValidator.validateUpdateRequest(request);
        
        // Find existing task
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        // Update task fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        
        // Save updated task
        Task updatedTask = taskDataStore.save(task);
        
        logger.info("Task updated successfully with id: {}", updatedTask.getId());
        return TaskResponse.from(updatedTask);
    }

    @Override
    @Retryable(maxAttempts = 3)
    public void deleteTask(Long id) {
        logger.debug("Deleting task with id: {}", id);
        
        // Check if task exists
        if (!taskDataStore.findById(id).isPresent()) {
            throw new TaskNotFoundException(id);
        }
        
        // Delete task
        boolean deleted = taskDataStore.deleteById(id);
        
        if (deleted) {
            logger.info("Task deleted successfully with id: {}", id);
        } else {
            logger.error("Failed to delete task with id: {}", id);
            throw new RuntimeException("Failed to delete task");
        }
    }

    @Override
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        logger.debug("Fetching tasks with status: {}", status);
        
        List<Task> tasks = taskDataStore.findByStatus(status);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        logger.debug("Fetching all tasks");
        
        List<Task> tasks = taskDataStore.findAll();
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }
}
