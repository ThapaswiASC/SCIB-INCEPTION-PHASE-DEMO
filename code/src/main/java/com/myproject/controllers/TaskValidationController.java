package com.myproject.controllers;

import com.myproject.models.dtos.TaskCreateRequestValidated;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.ValidationResponse;
import com.myproject.services.interfaces.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskValidationController {

    private final TaskService taskService;

    public TaskValidationController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTaskWithValidation(
            @Valid @RequestBody TaskCreateRequestValidated request) {
        TaskResponse response = taskService.createTaskValidated(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateTaskInput(
            @Valid @RequestBody TaskCreateRequestValidated request) {
        ValidationResponse response = taskService.validateTaskInput(request);
        return ResponseEntity.ok(response);
    }
}