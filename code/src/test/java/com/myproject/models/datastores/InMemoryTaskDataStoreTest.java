package com.myproject.models.datastores;

import com.myproject.models.dtos.Priority;
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
    void save_NewTask_AssignsIdAndSaves() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        task.setPriority(Priority.HIGH);

        // Act
        Task savedTask = dataStore.save(task);

        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        assertEquals(1L, savedTask.getUserId());
    }

    @Test
    void save_ExistingTask_UpdatesTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Original Title");
        task.setUserId(1L);
        task.setPriority(Priority.HIGH);
        Task savedTask = dataStore.save(task);

        // Act
        savedTask.setTitle("Updated Title");
        Task updatedTask = dataStore.save(savedTask);

        // Assert
        assertEquals(savedTask.getId(), updatedTask.getId());
        assertEquals("Updated Title", updatedTask.getTitle());
    }

    @Test
    void save_MultipleTasksSequentially_AssignsIncrementingIds() {
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
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertEquals(saved1.getId() + 1, saved2.getId());
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

    // ========== findByUserId Tests ==========

    @Test
    void findByUserId_MultipleTasksForUser_ReturnsAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);
        task1.setCreatedAt(LocalDateTime.now().minusDays(2));

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(1L);
        task2.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setUserId(2L);

        dataStore.save(task1);
        dataStore.save(task2);
        dataStore.save(task3);

        // Act
        List<Task> userTasks = dataStore.findByUserId(1L, 0, 10);

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
            task.setUserId(1L);
            task.setCreatedAt(LocalDateTime.now().minusDays(5 - i));
            dataStore.save(task);
        }

        // Act
        List<Task> page1 = dataStore.findByUserId(1L, 0, 2);
        List<Task> page2 = dataStore.findByUserId(1L, 1, 2);

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
        List<Task> tasks = dataStore.findByUserId(999L, 0, 10);

        // Assert
        assertTrue(tasks.isEmpty());
    }

    // ========== findAll Tests ==========

    @Test
    void findAll_MultipleTasks_ReturnsAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(2L);

        dataStore.save(task1);
        dataStore.save(task2);

        // Act
        List<Task> allTasks = dataStore.findAll();

        // Assert
        assertEquals(2, allTasks.size());
    }

    @Test
    void findAll_NoTasks_ReturnsEmptyList() {
        // Act
        List<Task> allTasks = dataStore.findAll();

        // Assert
        assertTrue(allTasks.isEmpty());
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

    // ========== existsById Tests ==========

    @Test
    void existsById_ExistingTask_ReturnsTrue() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId(1L);
        Task savedTask = dataStore.save(task);

        // Act
        boolean exists = dataStore.existsById(savedTask.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_NonExistingTask_ReturnsFalse() {
        // Act
        boolean exists = dataStore.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    // ========== countByUserId Tests ==========

    @Test
    void countByUserId_MultipleTasksForUser_ReturnsCorrectCount() {
        // Arrange
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setUserId(1L);
            dataStore.save(task);
        }

        Task otherUserTask = new Task();
        otherUserTask.setTitle("Other Task");
        otherUserTask.setUserId(2L);
        dataStore.save(otherUserTask);

        // Act
        long count = dataStore.countByUserId(1L);

        // Assert
        assertEquals(3, count);
    }

    @Test
    void countByUserId_NoTasksForUser_ReturnsZero() {
        // Act
        long count = dataStore.countByUserId(999L);

        // Assert
        assertEquals(0, count);
    }

    // ========== saveAll Tests ==========

    @Test
    void saveAll_MultipleTasks_SavesAllAndAssignsIds() {
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
        assertEquals("Task 1", savedTasks.get(0).getTitle());
        assertEquals("Task 2", savedTasks.get(1).getTitle());
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
    void fullCrudCycle_WorksCorrectly() {
        // Create
        Task task = new Task();
        task.setTitle("CRUD Test Task");
        task.setUserId(1L);
        task.setPriority(Priority.HIGH);
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
}