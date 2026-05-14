package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        logger.info("POST /v1/tasks - Creating task for user: {}", request.getUserId());
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks() {
        logger.info("GET /v1/tasks - Listing all tasks");
        List<TaskResponse> tasks = taskService.listTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        logger.info("GET /v1/tasks/{} - Fetching task", taskId);
        TaskResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        logger.info("PUT /v1/tasks/{} - Updating task", taskId);
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        logger.info("DELETE /v1/tasks/{} - Deleting task", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<PagedTaskResponse> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        logger.info("GET /v1/users/{}/tasks - Fetching user tasks, page: {}, size: {}", userId, page, size);
        PagedTaskResponse response = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        logger.info("GET /v1/users/{}/tasks/count - Getting task count", userId);
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<TaskCreateRequest> requests) {
        logger.info("POST /v1/tasks/bulk - Bulk creating {} tasks", requests.size());
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<UpdateTaskStatusResponse> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        logger.info("PUT /v1/tasks/{}/status - Updating task status", taskId);
        UpdateTaskStatusResponse response = taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDetailsResponse> getTaskDetails(@PathVariable String taskId) {
        logger.info("GET /v1/tasks/{} - Fetching task details", taskId);
        TaskDetailsResponse response = taskService.getTaskDetails(taskId);
        return ResponseEntity.ok(response);
    }
}