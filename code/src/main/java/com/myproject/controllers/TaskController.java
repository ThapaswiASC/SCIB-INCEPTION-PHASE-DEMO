package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        log.info("Creating task for user: {}", request.getUserId());
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailsResponse> getTaskDetails(@PathVariable String taskId) {
        log.info("Getting task details for taskId: {}", taskId);
        TaskDetailsResponse response = taskService.getTaskDetails(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskStatusUpdateResponse> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        log.info("Updating task status for taskId: {}", taskId);
        TaskStatusUpdateResponse response = taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/update")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        log.info("Updating task: {}", taskId);
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}/delete")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        log.info("Deleting task: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkTaskResponse> bulkCreateTasks(
            @Valid @RequestBody List<TaskCreateRequest> requests) {
        log.info("Bulk creating {} tasks", requests.size());
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}