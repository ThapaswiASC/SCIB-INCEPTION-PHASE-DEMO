package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDataStore taskDataStore;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "taskUserLimit", 10000);
        ReflectionTestUtils.setField(taskService, "performanceThreshold", 200L);
    }

    // ========== createTask Tests ==========

    @Test
    void createTask_ValidRequest_ReturnsTaskResponse() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            1L,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setUserId(1L);
        savedTask.setPriority(Priority.HIGH);
        savedTask.setStatus(TaskStatus.PENDING);
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.countByUserId(1L)).thenReturn(0L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(1L, response.getUserId());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(TaskStatus.PENDING, response.getStatus());

        verify(taskDataStore, times(1)).countByUserId(1L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_TaskLimitExceeded_ThrowsException() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            1L,
            Priority.HIGH,
            null
        );

        when(taskDataStore.countByUserId(1L)).thenReturn(10000L);

        // Act & Assert
        TaskLimitExceededException exception = assertThrows(
            TaskLimitExceededException.class,
            () -> taskService.createTask(request)
        );

        assertTrue(exception.getMessage().contains("User 1 has reached the maximum limit of 10000 tasks"));
        verify(taskDataStore, times(1)).countByUserId(1L);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void createTask_NearLimit_SuccessfullyCreates() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            1L,
            Priority.HIGH,
            null
        );

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setUserId(1L);
        savedTask.setPriority(Priority.HIGH);
        savedTask.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId(1L)).thenReturn(9999L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(taskDataStore, times(1)).countByUserId(1L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    // ========== getUserTasks Tests ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsTaskList() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setUserId(1L);
        task1.setPriority(Priority.HIGH);
        task1.setStatus(TaskStatus.PENDING);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setUserId(1L);
        task2.setPriority(Priority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskDataStore.findByUserId(1L, 0, 20)).thenReturn(tasks);

        // Act
        List<TaskResponse> responses = taskService.getUserTasks(1L, 0, 20);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Task 1", responses.get(0).getTitle());
        assertEquals("Task 2", responses.get(1).getTitle());

        verify(taskDataStore, times(1)).findByUserId(1L, 0, 20);
    }

    @Test
    void getUserTasks_NoTasks_ReturnsEmptyList() {
        // Arrange
        when(taskDataStore.findByUserId(1L, 0, 20)).thenReturn(Collections.emptyList());

        // Act
        List<TaskResponse> responses = taskService.getUserTasks(1L, 0, 20);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(taskDataStore, times(1)).findByUserId(1L, 0, 20);
    }

    // ========== getTaskCount Tests ==========

    @Test
    void getTaskCount_ValidUserId_ReturnsCount() {
        // Arrange
        when(taskDataStore.countByUserId(1L)).thenReturn(42L);

        // Act
        TaskCountResponse response = taskService.getTaskCount(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(42, response.getTaskCount());

        verify(taskDataStore, times(1)).countByUserId(1L);
    }

    @Test
    void getTaskCount_NoTasks_ReturnsZero() {
        // Arrange
        when(taskDataStore.countByUserId(1L)).thenReturn(0L);

        // Act
        TaskCountResponse response = taskService.getTaskCount(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(0, response.getTaskCount());

        verify(taskDataStore, times(1)).countByUserId(1L);
    }

    // ========== getTaskById Tests ==========

    @Test
    void getTaskById_ValidId_ReturnsTask() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setUserId(1L);
        task.setPriority(Priority.HIGH);
        task.setStatus(TaskStatus.PENDING);

        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));

        // Act
        TaskResponse response = taskService.getTaskById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());

        verify(taskDataStore, times(1)).findById(1L);
    }

    @Test
    void getTaskById_TaskNotFound_ThrowsException() {
        // Arrange
        when(taskDataStore.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.getTaskById(999L)
        );

        assertTrue(exception.getMessage().contains("Task with ID 999 not found"));
        verify(taskDataStore, times(1)).findById(999L);
    }

    // ========== updateTask Tests ==========

    @Test
    void updateTask_ValidRequest_ReturnsUpdatedTask() {
        // Arrange
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setUserId(1L);
        existingTask.setPriority(Priority.LOW);
        existingTask.setStatus(TaskStatus.PENDING);

        TaskUpdateRequest request = new TaskUpdateRequest(
            "New Title",
            "New Description",
            Priority.HIGH,
            TaskStatus.COMPLETED,
            LocalDateTime.now().plusDays(1)
        );

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setUserId(1L);
        updatedTask.setPriority(Priority.HIGH);
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(taskDataStore.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskResponse response = taskService.updateTask(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Title", response.getTitle());
        assertEquals("New Description", response.getDescription());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(TaskStatus.COMPLETED, response.getStatus());

        verify(taskDataStore, times(1)).findById(1L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_PartialUpdate_OnlyUpdatesProvidedFields() {
        // Arrange
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setUserId(1L);
        existingTask.setPriority(Priority.LOW);
        existingTask.setStatus(TaskStatus.PENDING);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New Title");
        // Only title is set, other fields are null

        when(taskDataStore.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskDataStore.save(any(Task.class))).thenReturn(existingTask);

        // Act
        TaskResponse response = taskService.updateTask(1L, request);

        // Assert
        assertNotNull(response);
        verify(taskDataStore, times(1)).findById(1L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound_ThrowsException() {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest(
            "New Title",
            "New Description",
            Priority.HIGH,
            TaskStatus.COMPLETED,
            null
        );

        when(taskDataStore.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.updateTask(999L, request)
        );

        assertTrue(exception.getMessage().contains("Task with ID 999 not found"));
        verify(taskDataStore, times(1)).findById(999L);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    // ========== deleteTask Tests ==========

    @Test
    void deleteTask_ValidId_DeletesTask() {
        // Arrange
        when(taskDataStore.existsById(1L)).thenReturn(true);
        doNothing().when(taskDataStore).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskDataStore, times(1)).existsById(1L);
        verify(taskDataStore, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsException() {
        // Arrange
        when(taskDataStore.existsById(999L)).thenReturn(false);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.deleteTask(999L)
        );

        assertTrue(exception.getMessage().contains("Task with ID 999 not found"));
        verify(taskDataStore, times(1)).existsById(999L);
        verify(taskDataStore, never()).deleteById(anyLong());
    }

    // ========== bulkCreateTasks Tests ==========

    @Test
    void bulkCreateTasks_AllSuccessful_ReturnsSuccessResponse() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", 1L, Priority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", 1L, Priority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(1L);
        savedTask1.setTitle("Task 1");
        savedTask1.setUserId(1L);
        savedTask1.setPriority(Priority.HIGH);
        savedTask1.setStatus(TaskStatus.PENDING);

        Task savedTask2 = new Task();
        savedTask2.setId(2L);
        savedTask2.setTitle("Task 2");
        savedTask2.setUserId(1L);
        savedTask2.setPriority(Priority.MEDIUM);
        savedTask2.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId(1L)).thenReturn(0L);
        when(taskDataStore.save(any(Task.class)))
            .thenReturn(savedTask1)
            .thenReturn(savedTask2);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertEquals(2, response.getTasks().size());
        assertTrue(response.getErrors().isEmpty());

        verify(taskDataStore, times(2)).countByUserId(1L);
        verify(taskDataStore, times(2)).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_SomeFailures_ReturnsPartialSuccess() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", 1L, Priority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", 2L, Priority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(1L);
        savedTask1.setTitle("Task 1");
        savedTask1.setUserId(1L);
        savedTask1.setPriority(Priority.HIGH);
        savedTask1.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId(1L)).thenReturn(0L);
        when(taskDataStore.countByUserId(2L)).thenReturn(10000L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask1);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals(1, response.getTasks().size());
        assertEquals(1, response.getErrors().size());

        verify(taskDataStore, times(1)).countByUserId(1L);
        verify(taskDataStore, times(1)).countByUserId(2L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_EmptyList_ReturnsEmptyResponse() {
        // Arrange
        List<TaskCreateRequest> requests = Collections.emptyList();

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertTrue(response.getTasks().isEmpty());
        assertTrue(response.getErrors().isEmpty());

        verify(taskDataStore, never()).countByUserId(anyLong());
        verify(taskDataStore, never()).save(any(Task.class));
    }
}