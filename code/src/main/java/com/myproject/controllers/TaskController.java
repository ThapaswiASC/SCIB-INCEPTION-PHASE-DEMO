package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import com.myproject.utils.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        String userId = authenticationHelper.getCurrentUserId();
        TaskResponse response = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/tasks/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(@Valid @RequestBody List<TaskCreateRequest> requests) {
        String userId = authenticationHelper.getCurrentUserId();
        BulkTaskResponse response = taskService.bulkCreateTasks(userId, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        String userId = authenticationHelper.getCurrentUserId();
        TaskResponse response = taskService.getTaskById(taskId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        String userId = authenticationHelper.getCurrentUserId();
        TaskResponse response = taskService.updateTask(taskId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        String userId = authenticationHelper.getCurrentUserId();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskResponse>> getUserTasks(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        List<TaskResponse> response = taskService.getUserTasks(userId, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable String userId) {
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }
}