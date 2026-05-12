package com.myproject.controllers;

import com.myproject.models.dtos.PagedTaskResponse;
import com.myproject.models.dtos.TaskCountResponse;
import com.myproject.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserTaskController {
    private final TaskService taskService;

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<PagedTaskResponse> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Getting tasks for user: {} (page: {}, size: {})", userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        PagedTaskResponse response = taskService.getUserTasks(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        log.info("Getting task count for user: {}", userId);
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }
}