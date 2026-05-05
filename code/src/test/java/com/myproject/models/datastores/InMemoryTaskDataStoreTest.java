package com.myproject.models.datastores;

import com.myproject.models.dtos.TaskPriority;
import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskDataStoreTest {

    private InMemoryTaskDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryTaskDataStore();
    }

    // ========== save Tests ==========

    @Test
    void save_NewTask_AssignsIdAndTimestamps() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        task.setStatus(TaskStatus.TO_DO);
        task.setPriority(TaskPriority.HIGH);

        // Act
        Task savedTask = dataStore.save(task);

        // Assert
        assertNotNull(savedTask.getId());
        assertEquals(1L, savedTask.getId());
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getUpdatedAt());
        assertEquals(1L, savedTask.getVersion());
    }

    @Test
    void save_ExistingTask_UpdatesVersionAndTimestamp() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        Task savedTask = dataStore.save(task);
        Long originalVersion = savedTask.getVersion();
        LocalDateTime originalUpdatedAt = savedTask.getUpdatedAt();

        // Act
        savedTask.setTitle("Updated Task");
        Task updatedTask = dataStore.save(savedTask);

        // Assert
        assertEquals(originalVersion + 1, updatedTask.getVersion());
        assertTrue(updatedTask.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   updatedTask.getUpdatedAt().isEqual(originalUpdatedAt));
    }

    @Test
    void save_MultipleTasks_AssignsIncrementalIds() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(1L);

        // Act
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        // Assert
        assertEquals(1L, saved1.getId());
        assertEquals(2L, saved2.getId());
    }

    // ========== findById Tests ==========

    @Test
    void findById_ExistingTask_ReturnsTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
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
        Optional<Task> found = dataStore.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findById_StringId_ReturnsTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        Task savedTask = dataStore.save(task);

        // Act
        Optional<Task> found = dataStore.findById("1");

        // Assert
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void findById_InvalidStringId_ReturnsEmpty() {
        // Act
        Optional<Task> found = dataStore.findById("invalid");

        // Assert
        assertFalse(found.isPresent());
    }

    // ========== findByUserId Tests ==========

    @Test
    void findByUserId_ExistingTasks_ReturnsPaginatedList() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }

        // Act
        List<Task> tasks = dataStore.findByUserId(1L, 0, 3);

        // Assert
        assertEquals(3, tasks.size());
    }

    @Test
    void findByUserId_SecondPage_ReturnsRemainingTasks() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }

        // Act
        List<Task> tasks = dataStore.findByUserId(1L, 1, 3);

        // Assert
        assertEquals(2, tasks.size());
    }

    @Test
    void findByUserId_NoTasks_ReturnsEmptyList() {
        // Act
        List<Task> tasks = dataStore.findByUserId(999L, 0, 10);

        // Assert
        assertTrue(tasks.isEmpty());
    }

    @Test
    void findByUserId_SortedByCreatedAtDescending_ReturnsInOrder() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);
        dataStore.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(1L);
        dataStore.save(task2);

        // Act
        List<Task> tasks = dataStore.findByUserId(1L, 0, 10);

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getCreatedAt().isAfter(tasks.get(1).getCreatedAt()) ||
                   tasks.get(0).getCreatedAt().isEqual(tasks.get(1).getCreatedAt()));
    }

    // ========== countByUserId Tests ==========

    @Test
    void countByUserId_ExistingTasks_ReturnsCount() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }

        // Act
        Long count = dataStore.countByUserId(1L);

        // Assert
        assertEquals(5L, count);
    }

    @Test
    void countByUserId_NoTasks_ReturnsZero() {
        // Act
        Long count = dataStore.countByUserId(999L);

        // Assert
        assertEquals(0L, count);
    }

    @Test
    void countByUserId_MultipleUsers_ReturnsCorrectCount() {
        // Arrange
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("User1 Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }
        for (int i = 0; i < 2; i++) {
            Task task = new Task();
            task.setTitle("User2 Task " + i);
            task.setUserId(2L);
            dataStore.save(task);
        }

        // Act
        Long count1 = dataStore.countByUserId(1L);
        Long count2 = dataStore.countByUserId(2L);

        // Assert
        assertEquals(3L, count1);
        assertEquals(2L, count2);
    }

    // ========== deleteById Tests ==========

    @Test
    void deleteById_ExistingTask_RemovesTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
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
        assertDoesNotThrow(() -> dataStore.deleteById(999L));
    }

    // ========== findAll Tests ==========

    @Test
    void findAll_ExistingTasks_ReturnsAllTasks() {
        // Arrange
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }

        // Act
        List<Task> allTasks = dataStore.findAll();

        // Assert
        assertEquals(3, allTasks.size());
    }

    @Test
    void findAll_NoTasks_ReturnsEmptyList() {
        // Act
        List<Task> allTasks = dataStore.findAll();

        // Assert
        assertTrue(allTasks.isEmpty());
    }

    // ========== saveAll Tests ==========

    @Test
    void saveAll_MultipleTasks_SavesAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(1L);

        List<Task> tasks = Arrays.asList(task1, task2);

        // Act
        List<Task> savedTasks = dataStore.saveAll(tasks);

        // Assert
        assertEquals(2, savedTasks.size());
        assertNotNull(savedTasks.get(0).getId());
        assertNotNull(savedTasks.get(1).getId());
    }

    @Test
    void saveAll_EmptyList_ReturnsEmptyList() {
        // Act
        List<Task> savedTasks = dataStore.saveAll(Arrays.asList());

        // Assert
        assertTrue(savedTasks.isEmpty());
    }

    // ========== Integration Tests ==========

    @Test
    void fullCrudCycle_CreateReadUpdateDelete_WorksCorrectly() {
        // Create
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        task.setStatus(TaskStatus.TO_DO);
        task.setPriority(TaskPriority.HIGH);
        Task savedTask = dataStore.save(task);
        assertNotNull(savedTask.getId());

        // Read
        Optional<Task> found = dataStore.findById(savedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Task", found.get().getTitle());

        // Update
        found.get().setTitle("Updated Task");
        Task updatedTask = dataStore.save(found.get());
        assertEquals("Updated Task", updatedTask.getTitle());

        // Delete
        dataStore.deleteById(updatedTask.getId());
        Optional<Task> deleted = dataStore.findById(updatedTask.getId());
        assertFalse(deleted.isPresent());
    }
}