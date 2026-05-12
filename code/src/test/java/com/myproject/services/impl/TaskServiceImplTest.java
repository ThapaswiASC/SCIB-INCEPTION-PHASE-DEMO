package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskCounterDataStore;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.PerformanceMonitoringService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDataStore taskDataStore;

    @Mock
    private TaskCounterDataStore taskCounterDataStore;

    @Mock
    private PerformanceMonitoringService performanceMonitoringService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "maxTasksPerUser", 10000);
        ReflectionTestUtils.setField(taskService, "performanceThresholdMs", 200L);
    }

    // ========== createTask Tests ==========

    @Test
    void createTask_ValidRequest_ReturnsTaskResponse() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            TaskPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setUserId("user123");
        savedTask.setPriority(TaskPriority.HIGH);
        savedTask.setStatus(TaskStatus.PENDING);
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.countByUserId("user123")).thenReturn(0L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask("user123", request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("user123", response.getUserId());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertEquals(TaskStatus.PENDING, response.getStatus());

        verify(taskDataStore, times(1)).countByUserId("user123");
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_TaskLimitExceeded_ThrowsException() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            TaskPriority.HIGH,
            null
        );

        when(taskDataStore.countByUserId("user123")).thenReturn(10000L);

        // Act & Assert
        TaskLimitExceededException exception = assertThrows(
            TaskLimitExceededException.class,
            () -> taskService.createTask("user123", request)
        );

        assertTrue(exception.getMessage().contains("maximum task limit"));
        verify(taskDataStore, times(1)).countByUserId("user123");
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void createTask_NearLimit_SuccessfullyCreates() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            TaskPriority.HIGH,
            null
        );

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setTitle("Test Task");
        savedTask.setUserId("user123");
        savedTask.setPriority(TaskPriority.HIGH);
        savedTask.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId("user123")).thenReturn(9999L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask("user123", request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(taskDataStore, times(1)).countByUserId("user123");
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    // ========== getUserTasks Tests ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsTaskList() {
        // Arrange
        Task task1 = new Task();
        task1.setId(UUID.randomUUID());
        task1.setTitle("Task 1");
        task1.setUserId("user123");
        task1.setPriority(TaskPriority.HIGH);
        task1.setStatus(TaskStatus.PENDING);

        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Task 2");
        task2.setUserId("user123");
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskDataStore.findByUserId("user123", 0, 20, "createdAt,desc")).thenReturn(tasks);

        // Act
        List<TaskResponse> responses = taskService.getUserTasks("user123", 0, 20, "createdAt,desc");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Task 1", responses.get(0).getTitle());
        assertEquals("Task 2", responses.get(1).getTitle());

        verify(taskDataStore, times(1)).findByUserId("user123", 0, 20, "createdAt,desc");
    }

    @Test
    void getUserTasks_NoTasks_ReturnsEmptyList() {
        // Arrange
        when(taskDataStore.findByUserId("user123", 0, 20, "createdAt,desc")).thenReturn(Collections.emptyList());

        // Act
        List<TaskResponse> responses = taskService.getUserTasks("user123", 0, 20, "createdAt,desc");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(taskDataStore, times(1)).findByUserId("user123", 0, 20, "createdAt,desc");
    }

    // ========== getTaskCount Tests ==========

    @Test
    void getTaskCount_ValidUserId_ReturnsCount() {
        // Arrange
        when(taskDataStore.countByUserId("user123")).thenReturn(42L);

        // Act
        TaskCountResponse response = taskService.getTaskCount("user123");

        // Assert
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        assertEquals(42, response.getTaskCount());

        verify(taskDataStore, times(1)).countByUserId("user123");
    }

    @Test
    void getTaskCount_NoTasks_ReturnsZero() {
        // Arrange
        when(taskDataStore.countByUserId("user123")).thenReturn(0L);

        // Act
        TaskCountResponse response = taskService.getTaskCount("user123");

        // Assert
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        assertEquals(0, response.getTaskCount());

        verify(taskDataStore, times(1)).countByUserId("user123");
    }

    // ========== getTaskById Tests ==========

    @Test
    void getTaskById_ValidId_ReturnsTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setUserId("user123");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.PENDING);

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        TaskResponse response = taskService.getTaskById(taskId, "user123");

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("Test Task", response.getTitle());

        verify(taskDataStore, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_TaskNotFound_ThrowsException() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.getTaskById(taskId, "user123")
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(taskDataStore, times(1)).findById(taskId);
    }

    // ========== updateTask Tests ==========

    @Test
    void updateTask_ValidRequest_ReturnsUpdatedTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setUserId("user123");
        existingTask.setPriority(TaskPriority.LOW);
        existingTask.setStatus(TaskStatus.PENDING);

        TaskUpdateRequest request = new TaskUpdateRequest(
            "New Title",
            "New Description",
            TaskPriority.HIGH,
            TaskStatus.COMPLETED,
            LocalDateTime.now().plusDays(1)
        );

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setUserId("user123");
        updatedTask.setPriority(TaskPriority.HIGH);
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskResponse response = taskService.updateTask(taskId, "user123", request);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("New Title", response.getTitle());
        assertEquals("New Description", response.getDescription());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertEquals(TaskStatus.COMPLETED, response.getStatus());

        verify(taskDataStore, times(1)).findById(taskId);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_PartialUpdate_OnlyUpdatesProvidedFields() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setUserId("user123");
        existingTask.setPriority(TaskPriority.LOW);
        existingTask.setStatus(TaskStatus.PENDING);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New Title");

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskDataStore.save(any(Task.class))).thenReturn(existingTask);

        // Act
        TaskResponse response = taskService.updateTask(taskId, "user123", request);

        // Assert
        assertNotNull(response);
        verify(taskDataStore, times(1)).findById(taskId);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound_ThrowsException() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskUpdateRequest request = new TaskUpdateRequest(
            "New Title",
            "New Description",
            TaskPriority.HIGH,
            TaskStatus.COMPLETED,
            null
        );

        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.updateTask(taskId, "user123", request)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(taskDataStore, times(1)).findById(taskId);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    // ========== deleteTask Tests ==========

    @Test
    void deleteTask_ValidId_DeletesTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setUserId("user123");
        
        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskDataStore).deleteById(taskId);

        // Act
        taskService.deleteTask(taskId, "user123");

        // Assert
        verify(taskDataStore, times(1)).findById(taskId);
        verify(taskDataStore, times(1)).deleteById(taskId);
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsException() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.deleteTask(taskId, "user123")
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(taskDataStore, times(1)).findById(taskId);
        verify(taskDataStore, never()).deleteById(any(UUID.class));
    }

    // ========== bulkCreateTasks Tests ==========

    @Test
    void bulkCreateTasks_AllSuccessful_ReturnsSuccessResponse() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", null, TaskPriority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", null, TaskPriority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(UUID.randomUUID());
        savedTask1.setTitle("Task 1");
        savedTask1.setUserId("user123");
        savedTask1.setPriority(TaskPriority.HIGH);
        savedTask1.setStatus(TaskStatus.PENDING);

        Task savedTask2 = new Task();
        savedTask2.setId(UUID.randomUUID());
        savedTask2.setTitle("Task 2");
        savedTask2.setUserId("user123");
        savedTask2.setPriority(TaskPriority.MEDIUM);
        savedTask2.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId("user123")).thenReturn(0L);
        when(taskDataStore.save(any(Task.class)))
            .thenReturn(savedTask1)
            .thenReturn(savedTask2);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks("user123", requests);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertEquals(2, response.getTasks().size());
        assertTrue(response.getErrors().isEmpty());

        verify(taskDataStore, times(2)).countByUserId("user123");
        verify(taskDataStore, times(2)).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_SomeFailures_ReturnsPartialSuccess() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", null, TaskPriority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", null, TaskPriority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(UUID.randomUUID());
        savedTask1.setTitle("Task 1");
        savedTask1.setUserId("user123");
        savedTask1.setPriority(TaskPriority.HIGH);
        savedTask1.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId("user123")).thenReturn(0L).thenReturn(10000L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask1);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks("user123", requests);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals(1, response.getTasks().size());
        assertEquals(1, response.getErrors().size());

        verify(taskDataStore, times(2)).countByUserId("user123");
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_EmptyList_ReturnsEmptyResponse() {
        // Arrange
        List<TaskCreateRequest> requests = Collections.emptyList();

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks("user123", requests);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertTrue(response.getTasks().isEmpty());
        assertTrue(response.getErrors().isEmpty());

        verify(taskDataStore, never()).countByUserId(anyString());
        verify(taskDataStore, never()).save(any(Task.class));
    }
}