package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        logger.info("Creating task for user: {}", request.getUserId());
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskResponse>> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        logger.info("Fetching tasks for user: {}, page: {}, size: {}", userId, page, size);
        List<TaskResponse> tasks = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        logger.info("Fetching task count for user: {}", userId);
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        logger.info("Fetching task by ID: {}", taskId);
        TaskResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        logger.info("Updating task: {}", taskId);
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        logger.info("Deleting task: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<@Valid TaskCreateRequest> requests) {
        logger.info("Bulk creating {} tasks", requests.size());
        
        if (requests.size() > 100) {
            throw new IllegalArgumentException("Bulk request cannot exceed 100 tasks");
        }
        
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}