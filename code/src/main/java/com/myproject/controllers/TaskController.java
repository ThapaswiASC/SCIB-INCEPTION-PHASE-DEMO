package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<UpdateTaskStatusResponse> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        UpdateTaskStatusResponse response = taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDetailsResponse> getTaskDetails(@PathVariable String taskId) {
        TaskDetailsResponse response = taskService.getTaskDetails(taskId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<PagedTaskResponse> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PagedTaskResponse response = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/tasks/{taskId}/update")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/tasks/{taskId}/delete")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/tasks/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<TaskCreateRequest> requests) {
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}