package com.myproject.utils;

import com.myproject.models.dtos.TaskPriority;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.entities.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    @Test
    void toResponse_ValidTask_ReturnsTaskResponse() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(7);
        
        Task task = new Task();
        task.setId(taskId);
        task.setUserId("user123");
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setDueDate(dueDate);
        task.setVersion(1L);

        // Act
        TaskResponse response = TaskMapper.toResponse(task);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(1L, response.getVersion());
    }

    @Test
    void toResponse_NullTask_ReturnsNull() {
        // Act
        TaskResponse response = TaskMapper.toResponse(null);

        // Assert
        assertNull(response);
    }

    @Test
    void toResponse_TaskWithNullOptionalFields_ReturnsResponseWithNulls() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Task task = new Task();
        task.setId(taskId);
        task.setUserId("user123");
        task.setTitle("Minimal Task");
        task.setDescription(null);
        task.setPriority(TaskPriority.LOW);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setDueDate(null);
        task.setVersion(0L);

        // Act
        TaskResponse response = TaskMapper.toResponse(task);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("Minimal Task", response.getTitle());
        assertNull(response.getDescription());
        assertNull(response.getDueDate());
    }

    @Test
    void toResponse_TaskWithAllPriorities_MapsCorrectly() {
        // Test all priority levels
        for (TaskPriority priority : TaskPriority.values()) {
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setUserId("user123");
            task.setTitle("Task");
            task.setPriority(priority);
            task.setStatus(TaskStatus.PENDING);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            TaskResponse response = TaskMapper.toResponse(task);

            assertNotNull(response);
            assertEquals(priority, response.getPriority());
        }
    }

    @Test
    void toResponse_TaskWithAllStatuses_MapsCorrectly() {
        // Test all status values
        for (TaskStatus status : TaskStatus.values()) {
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setUserId("user123");
            task.setTitle("Task");
            task.setPriority(TaskPriority.MEDIUM);
            task.setStatus(status);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            TaskResponse response = TaskMapper.toResponse(task);

            assertNotNull(response);
            assertEquals(status, response.getStatus());
        }
    }
}