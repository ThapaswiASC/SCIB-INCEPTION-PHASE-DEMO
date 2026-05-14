package com.myproject.controllers;

import com.myproject.models.dtos.PagedTaskResponse;
import com.myproject.models.dtos.TaskCountResponse;
import com.myproject.services.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserTaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<PagedTaskResponse> getUserTasksByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PagedTaskResponse response = taskService.getUserTasks(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/tasks/count")
    public ResponseEntity<TaskCountResponse> getTaskCount(@PathVariable Long userId) {
        TaskCountResponse response = taskService.getTaskCount(userId);
        return ResponseEntity.ok(response);
    }
}
