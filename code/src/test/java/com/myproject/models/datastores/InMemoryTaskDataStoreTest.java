package com.myproject.models.datastores;

import com.myproject.models.dtos.TaskPriority;
import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskDataStoreTest {

    private InMemoryTaskDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryTaskDataStore();
    }

    // ========== save Tests ==========

    @Test
    void save_NewTask_AssignsIdAndSaves() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user123");
        task.setPriority(TaskPriority.HIGH);

        // Act
        Task savedTask = dataStore.save(task);

        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        assertEquals("user123", savedTask.getUserId());
    }

    @Test
    void save_ExistingTask_UpdatesTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Original Title");
        task.setUserId("user123");
        task.setPriority(TaskPriority.HIGH);
        Task savedTask = dataStore.save(task);

        // Act
        savedTask.setTitle("Updated Title");
        Task updatedTask = dataStore.save(savedTask);

        // Assert
        assertEquals(savedTask.getId(), updatedTask.getId());
        assertEquals("Updated Title", updatedTask.getTitle());
    }

    @Test
    void save_MultipleTasksSequentially_AssignsUniqueIds() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId("user123");

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId("user123");

        // Act
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        // Assert
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
    }

    // ========== findById Tests ==========

    @Test
    void findById_ExistingTask_ReturnsTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user123");
        Task savedTask = dataStore.save(task);

        // Act
        Optional<Task> found = dataStore.findById(savedTask.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(savedTask.getId(), found.get().getId());
        assertEquals("Test Task", found.get().getTitle());
    }

    @Test
    void findById_NonExistingTask_ReturnsEmpty() {
        // Act
        Optional<Task> found = dataStore.findById(UUID.randomUUID());

        // Assert
        assertFalse(found.isPresent());
    }

    // ========== findByUserId Tests ==========

    @Test
    void findByUserId_MultipleTasksForUser_ReturnsAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId("user123");
        task1.setCreatedAt(LocalDateTime.now().minusDays(2));

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId("user123");
        task2.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setUserId("user456");

        dataStore.save(task1);
        dataStore.save(task2);
        dataStore.save(task3);

        // Act
        List<Task> userTasks = dataStore.findByUserId("user123", 0, 10, "createdAt,desc");

        // Assert
        assertEquals(2, userTasks.size());
        // Should be sorted by createdAt descending
        assertEquals("Task 2", userTasks.get(0).getTitle());
        assertEquals("Task 1", userTasks.get(1).getTitle());
    }

    @Test
    void findByUserId_WithPagination_ReturnsCorrectPage() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId("user123");
            task.setCreatedAt(LocalDateTime.now().minusDays(5 - i));
            dataStore.save(task);
        }

        // Act
        List<Task> page1 = dataStore.findByUserId("user123", 0, 2, "createdAt,desc");
        List<Task> page2 = dataStore.findByUserId("user123", 1, 2, "createdAt,desc");

        // Assert
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertEquals("Task 5", page1.get(0).getTitle());
        assertEquals("Task 4", page1.get(1).getTitle());
        assertEquals("Task 3", page2.get(0).getTitle());
        assertEquals("Task 2", page2.get(1).getTitle());
    }

    @Test
    void findByUserId_NoTasksForUser_ReturnsEmptyList() {
        // Act
        List<Task> tasks = dataStore.findByUserId("nonexistent", 0, 10, "createdAt,desc");

        // Assert
        assertTrue(tasks.isEmpty());
    }

    // ========== deleteById Tests ==========

    @Test
    void deleteById_ExistingTask_RemovesTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user123");
        Task savedTask = dataStore.save(task);

        // Act
        dataStore.deleteById(savedTask.getId());

        // Assert
        Optional<Task> found = dataStore.findById(savedTask.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_NonExistingTask_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> dataStore.deleteById(UUID.randomUUID()));
    }

    // ========== countByUserId Tests ==========

    @Test
    void countByUserId_MultipleTasksForUser_ReturnsCorrectCount() {
        // Arrange
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId("user123");
            dataStore.save(task);
        }

        Task otherUserTask = new Task();
        otherUserTask.setTitle("Other Task");
        otherUserTask.setUserId("user456");
        dataStore.save(otherUserTask);

        // Act
        long count = dataStore.countByUserId("user123");

        // Assert
        assertEquals(3, count);
    }

    @Test
    void countByUserId_NoTasksForUser_ReturnsZero() {
        // Act
        long count = dataStore.countByUserId("nonexistent");

        // Assert
        assertEquals(0, count);
    }

    // ========== Integration Tests ==========

    @Test
    void fullCrudCycle_WorksCorrectly() {
        // Create
        Task task = new Task();
        task.setTitle("CRUD Test Task");
        task.setUserId("user123");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.PENDING);
        Task savedTask = dataStore.save(task);
        assertNotNull(savedTask.getId());

        // Read
        Optional<Task> foundTask = dataStore.findById(savedTask.getId());
        assertTrue(foundTask.isPresent());
        assertEquals("CRUD Test Task", foundTask.get().getTitle());

        // Update
        foundTask.get().setTitle("Updated CRUD Task");
        Task updatedTask = dataStore.save(foundTask.get());
        assertEquals("Updated CRUD Task", updatedTask.getTitle());

        // Delete
        dataStore.deleteById(savedTask.getId());
        Optional<Task> deletedTask = dataStore.findById(savedTask.getId());
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void existsByIdAndUserId_ExistingTaskAndMatchingUser_ReturnsTrue() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user123");
        Task savedTask = dataStore.save(task);

        // Act
        boolean exists = dataStore.existsByIdAndUserId(savedTask.getId(), "user123");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByIdAndUserId_ExistingTaskButDifferentUser_ReturnsFalse() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user123");
        Task savedTask = dataStore.save(task);

        // Act
        boolean exists = dataStore.existsByIdAndUserId(savedTask.getId(), "user456");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByIdAndUserId_NonExistingTask_ReturnsFalse() {
        // Act
        boolean exists = dataStore.existsByIdAndUserId(UUID.randomUUID(), "user123");

        // Assert
        assertFalse(exists);
    }
}