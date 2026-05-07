package com.myproject.models.datastores;

import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskDataStoreTest {

    private InMemoryTaskDataStore dataStore;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryTaskDataStore();

        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setCreatedAt(LocalDateTime.now());

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setCreatedAt(LocalDateTime.now());
    }

    // ========== SAVE TESTS ==========

    @Test
    void save_NewTask_AssignsIdAndReturnsTask() {
        Task savedTask = dataStore.save(task1);

        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());
        assertEquals("Task 1", savedTask.getTitle());
        assertEquals("Description 1", savedTask.getDescription());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    @Test
    void save_NewTask_AssignsIncrementalId() {
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertTrue(saved2.getId() > saved1.getId());
    }

    @Test
    void save_ExistingTask_UpdatesTask() {
        Task savedTask = dataStore.save(task1);
        Long originalId = savedTask.getId();

        savedTask.setTitle("Updated Title");
        savedTask.setStatus(TaskStatus.COMPLETED);
        Task updatedTask = dataStore.save(savedTask);

        assertEquals(originalId, updatedTask.getId());
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals(TaskStatus.COMPLETED, updatedTask.getStatus());
    }

    @Test
    void save_MultipleNewTasks_AllStoredSuccessfully() {
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        List<Task> allTasks = dataStore.findAll();
        assertEquals(2, allTasks.size());
    }

    // ========== FIND BY ID TESTS ==========

    @Test
    void findById_ExistingTask_ReturnsTask() {
        Task savedTask = dataStore.save(task1);

        Optional<Task> found = dataStore.findById(savedTask.getId());

        assertTrue(found.isPresent());
        assertEquals(savedTask.getId(), found.get().getId());
        assertEquals("Task 1", found.get().getTitle());
    }

    @Test
    void findById_NonExistentTask_ReturnsEmpty() {
        Optional<Task> found = dataStore.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findById_AfterSavingMultipleTasks_ReturnsCorrectTask() {
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        Optional<Task> found = dataStore.findById(saved2.getId());

        assertTrue(found.isPresent());
        assertEquals("Task 2", found.get().getTitle());
    }

    // ========== FIND BY STATUS TESTS ==========

    @Test
    void findByStatus_WithMatchingTasks_ReturnsFilteredList() {
        task1.setStatus(TaskStatus.PENDING);
        task2.setStatus(TaskStatus.PENDING);
        dataStore.save(task1);
        dataStore.save(task2);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setDescription("Description 3");
        task3.setStatus(TaskStatus.COMPLETED);
        task3.setCreatedAt(LocalDateTime.now());
        dataStore.save(task3);

        List<Task> pendingTasks = dataStore.findByStatus(TaskStatus.PENDING);

        assertEquals(2, pendingTasks.size());
        assertTrue(pendingTasks.stream().allMatch(t -> t.getStatus() == TaskStatus.PENDING));
    }

    @Test
    void findByStatus_WithNoMatchingTasks_ReturnsEmptyList() {
        dataStore.save(task1);
        dataStore.save(task2);

        List<Task> completedTasks = dataStore.findByStatus(TaskStatus.COMPLETED);

        assertTrue(completedTasks.isEmpty());
    }

    @Test
    void findByStatus_WithEmptyDataStore_ReturnsEmptyList() {
        List<Task> tasks = dataStore.findByStatus(TaskStatus.PENDING);

        assertTrue(tasks.isEmpty());
    }

    // ========== DELETE BY ID TESTS ==========

    @Test
    void deleteById_ExistingTask_ReturnsTrue() {
        Task savedTask = dataStore.save(task1);

        boolean deleted = dataStore.deleteById(savedTask.getId());

        assertTrue(deleted);
        assertFalse(dataStore.findById(savedTask.getId()).isPresent());
    }

    @Test
    void deleteById_NonExistentTask_ReturnsFalse() {
        boolean deleted = dataStore.deleteById(999L);

        assertFalse(deleted);
    }

    @Test
    void deleteById_AfterDeletion_TaskNotFoundInDataStore() {
        Task savedTask = dataStore.save(task1);
        dataStore.deleteById(savedTask.getId());

        Optional<Task> found = dataStore.findById(savedTask.getId());

        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_DeletesOnlySpecifiedTask() {
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);

        dataStore.deleteById(saved1.getId());

        assertFalse(dataStore.findById(saved1.getId()).isPresent());
        assertTrue(dataStore.findById(saved2.getId()).isPresent());
    }

    // ========== FIND ALL TESTS ==========

    @Test
    void findAll_WithMultipleTasks_ReturnsAllTasks() {
        dataStore.save(task1);
        dataStore.save(task2);

        List<Task> allTasks = dataStore.findAll();

        assertEquals(2, allTasks.size());
    }

    @Test
    void findAll_WithEmptyDataStore_ReturnsEmptyList() {
        List<Task> allTasks = dataStore.findAll();

        assertTrue(allTasks.isEmpty());
    }

    @Test
    void findAll_AfterDeletion_ReturnsRemainingTasks() {
        Task saved1 = dataStore.save(task1);
        Task saved2 = dataStore.save(task2);
        dataStore.deleteById(saved1.getId());

        List<Task> allTasks = dataStore.findAll();

        assertEquals(1, allTasks.size());
        assertEquals(saved2.getId(), allTasks.get(0).getId());
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    void fullCrudCycle_SaveFindUpdateDelete_WorksCorrectly() {
        // Create
        Task savedTask = dataStore.save(task1);
        assertNotNull(savedTask.getId());

        // Read
        Optional<Task> found = dataStore.findById(savedTask.getId());
        assertTrue(found.isPresent());

        // Update
        found.get().setTitle("Updated Title");
        Task updated = dataStore.save(found.get());
        assertEquals("Updated Title", updated.getTitle());

        // Delete
        boolean deleted = dataStore.deleteById(savedTask.getId());
        assertTrue(deleted);
        assertFalse(dataStore.findById(savedTask.getId()).isPresent());
    }
}
