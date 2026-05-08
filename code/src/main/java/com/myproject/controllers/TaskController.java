package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskStatusUpdateResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        TaskStatusUpdateResponse response = taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDetailsResponse> getTaskById(@PathVariable Long taskId) {
        TaskDetailsResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskSummary>> getTasksByStatus(
            @RequestParam(required = true) String status) {
        List<TaskSummary> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks/{taskId}/validate-move")
    public ResponseEntity<ValidateTaskMoveResponse> validateTaskMove(
            @PathVariable Long taskId,
            @Valid @RequestBody ValidateTaskMoveRequest request) {
        ValidateTaskMoveResponse response = taskService.validateTaskMove(taskId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<PagedTaskResponse> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer size) {
        PagedTaskResponse response = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<TaskCreateRequest> requests) {
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}