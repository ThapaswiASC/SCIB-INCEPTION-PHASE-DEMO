package com.myproject.models.entities;

import com.myproject.models.dtos.TaskPriority;
import com.myproject.models.dtos.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void defaultConstructor_InitializesTimestampsAndStatus() {
        // Act
        Task task = new Task();

        // Assert
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNotNull(task.getId());
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertNull(task.getUserId());
        assertNull(task.getPriority());
        assertNull(task.getDueDate());
    }

    @Test
    void defaultConstructor_TimestampsAreRecent() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        Task task = new Task();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertTrue(task.getCreatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(task.getCreatedAt().isBefore(after.plusSeconds(1)));
        assertTrue(task.getUpdatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(task.getUpdatedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        Task task = new Task();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(5);
        UUID taskId = UUID.randomUUID();

        // Act
        task.setId(taskId);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setUserId("user200");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.COMPLETED);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setDueDate(dueDate);

        // Assert
        assertEquals(taskId, task.getId());
        assertEquals("Updated Title", task.getTitle());
        assertEquals("Updated Description", task.getDescription());
        assertEquals("user200", task.getUserId());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(now, task.getCreatedAt());
        assertEquals(now, task.getUpdatedAt());
        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void task_CanBeCreatedWithNullOptionalFields() {
        // Act
        Task task = new Task();
        task.setTitle("Minimal Task");
        task.setUserId("user100");
        task.setPriority(TaskPriority.LOW);
        task.setStatus(TaskStatus.PENDING);

        // Assert
        assertNotNull(task);
        assertEquals("Minimal Task", task.getTitle());
        assertNull(task.getDescription());
        assertNull(task.getDueDate());
    }
}