package com.myproject.models.entities;

import com.myproject.models.dtos.Priority;
import com.myproject.models.dtos.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        assertNull(task.getId());
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
    void parameterizedConstructor_SetsAllFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(7);

        // Act
        Task task = new Task(
            1L,
            "Test Task",
            "Test Description",
            100L,
            Priority.HIGH,
            TaskStatus.IN_PROGRESS,
            now,
            now,
            dueDate
        );

        // Assert
        assertEquals(1L, task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(100L, task.getUserId());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(now, task.getCreatedAt());
        assertEquals(now, task.getUpdatedAt());
        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        Task task = new Task();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(5);

        // Act
        task.setId(42L);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setUserId(200L);
        task.setPriority(Priority.CRITICAL);
        task.setStatus(TaskStatus.COMPLETED);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setDueDate(dueDate);

        // Assert
        assertEquals(42L, task.getId());
        assertEquals("Updated Title", task.getTitle());
        assertEquals("Updated Description", task.getDescription());
        assertEquals(200L, task.getUserId());
        assertEquals(Priority.CRITICAL, task.getPriority());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(now, task.getCreatedAt());
        assertEquals(now, task.getUpdatedAt());
        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void task_CanBeCreatedWithNullOptionalFields() {
        // Act
        Task task = new Task(
            1L,
            "Minimal Task",
            null,
            100L,
            Priority.LOW,
            TaskStatus.PENDING,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        // Assert
        assertNotNull(task);
        assertEquals("Minimal Task", task.getTitle());
        assertNull(task.getDescription());
        assertNull(task.getDueDate());
    }
}