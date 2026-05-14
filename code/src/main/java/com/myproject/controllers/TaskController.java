package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedTaskResponse> getUserTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        // For demo purposes, using a default userId. In production, extract from security context
        Long userId = 1L;
        PagedTaskResponse response = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<TaskCreateRequest> requests) {
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<UpdateTaskStatusResponse> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        UpdateTaskStatusResponse response = taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}/details")
    public ResponseEntity<TaskDetailsResponse> getTaskDetails(@PathVariable String taskId) {
        TaskDetailsResponse response = taskService.getTaskDetails(taskId);
        return ResponseEntity.ok(response);
    }
}
