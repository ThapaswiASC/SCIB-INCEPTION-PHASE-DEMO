package com.myproject.services.impl;

import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.exceptions.ValidationException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.utils.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDataStore taskDataStore;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private Task task;

    @BeforeEach
    void setUp() {
        createRequest = new TaskCreateRequest(
            "Complete project documentation",
            "Write comprehensive documentation for the task management system"
        );

        updateRequest = new TaskUpdateRequest(
            "Complete project documentation",
            "Write comprehensive documentation for the task management system",
            TaskStatus.IN_PROGRESS
        );

        task = new Task();
        task.setId(1L);
        task.setTitle("Complete project documentation");
        task.setDescription("Write comprehensive documentation for the task management system");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
    }

    // ========== CREATE TASK TESTS ==========

    @Test
    void createTask_WithValidRequest_ReturnsTaskResponse() {
        doNothing().when(taskValidator).validateCreateRequest(any(TaskCreateRequest.class));
        when(taskDataStore.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Complete project documentation", response.getTitle());
        assertEquals("Write comprehensive documentation for the task management system", response.getDescription());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertNotNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());

        verify(taskValidator, times(1)).validateCreateRequest(createRequest);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_WithInvalidRequest_ThrowsValidationException() {
        doThrow(new ValidationException("Validation failed"))
            .when(taskValidator).validateCreateRequest(any(TaskCreateRequest.class));

        assertThrows(ValidationException.class, () -> taskService.createTask(createRequest));

        verify(taskValidator, times(1)).validateCreateRequest(createRequest);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void createTask_SetsStatusToPending() {
        doNothing().when(taskValidator).validateCreateRequest(any(TaskCreateRequest.class));
        when(taskDataStore.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(createRequest);

        assertEquals(TaskStatus.PENDING, response.getStatus());
    }

    @Test
    void createTask_SetsCreatedAtTimestamp() {
        doNothing().when(taskValidator).validateCreateRequest(any(TaskCreateRequest.class));
        when(taskDataStore.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(createRequest);

        assertNotNull(response.getCreatedAt());
    }

    // ========== GET TASK BY ID TESTS ==========

    @Test
    void getTaskById_WithExistingId_ReturnsTaskResponse() {
        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Complete project documentation", response.getTitle());

        verify(taskDataStore, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WithNonExistentId_ThrowsTaskNotFoundException() {
        when(taskDataStore.findById(999L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> taskService.getTaskById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(taskDataStore, times(1)).findById(999L);
    }

    // ========== UPDATE TASK TESTS ==========

    @Test
    void updateTask_WithValidRequest_ReturnsUpdatedTaskResponse() {
        doNothing().when(taskValidator).validateUpdateRequest(any(TaskUpdateRequest.class));
        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
        assertNotNull(response.getUpdatedAt());

        verify(taskValidator, times(1)).validateUpdateRequest(updateRequest);
        verify(taskDataStore, times(1)).findById(1L);
        verify(taskDataStore, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WithNonExistentId_ThrowsTaskNotFoundException() {
        doNothing().when(taskValidator).validateUpdateRequest(any(TaskUpdateRequest.class));
        when(taskDataStore.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
            TaskNotFoundException.class,
            () -> taskService.updateTask(999L, updateRequest)
        );

        verify(taskValidator, times(1)).validateUpdateRequest(updateRequest);
        verify(taskDataStore, times(1)).findById(999L);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void updateTask_WithInvalidRequest_ThrowsValidationException() {
        doThrow(new ValidationException("Validation failed"))
            .when(taskValidator).validateUpdateRequest(any(TaskUpdateRequest.class));

        assertThrows(
            ValidationException.class,
            () -> taskService.updateTask(1L, updateRequest)
        );

        verify(taskValidator, times(1)).validateUpdateRequest(updateRequest);
        verify(taskDataStore, never()).findById(any(Long.class));
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void updateTask_UpdatesAllFields() {
        doNothing().when(taskValidator).validateUpdateRequest(any(TaskUpdateRequest.class));
        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertEquals(updateRequest.getTitle(), response.getTitle());
        assertEquals(updateRequest.getDescription(), response.getDescription());
        assertEquals(updateRequest.getStatus(), response.getStatus());
    }

    // ========== DELETE TASK TESTS ==========

    @Test
    void deleteTask_WithExistingId_DeletesSuccessfully() {
        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));
        when(taskDataStore.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));

        verify(taskDataStore, times(1)).findById(1L);
        verify(taskDataStore, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_WithNonExistentId_ThrowsTaskNotFoundException() {
        when(taskDataStore.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
            TaskNotFoundException.class,
            () -> taskService.deleteTask(999L)
        );

        verify(taskDataStore, times(1)).findById(999L);
        verify(taskDataStore, never()).deleteById(any(Long.class));
    }

    @Test
    void deleteTask_WhenDeleteFails_ThrowsRuntimeException() {
        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));
        when(taskDataStore.deleteById(1L)).thenReturn(false);

        assertThrows(
            RuntimeException.class,
            () -> taskService.deleteTask(1L)
        );

        verify(taskDataStore, times(1)).findById(1L);
        verify(taskDataStore, times(1)).deleteById(1L);
    }

    // ========== GET TASKS BY STATUS TESTS ==========

    @Test
    void getTasksByStatus_WithExistingTasks_ReturnsTaskList() {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.PENDING);
        task2.setCreatedAt(LocalDateTime.now());

        List<Task> tasks = Arrays.asList(task, task2);
        when(taskDataStore.findByStatus(TaskStatus.PENDING)).thenReturn(tasks);

        List<TaskResponse> responses = taskService.getTasksByStatus(TaskStatus.PENDING);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(TaskStatus.PENDING, responses.get(0).getStatus());
        assertEquals(TaskStatus.PENDING, responses.get(1).getStatus());

        verify(taskDataStore, times(1)).findByStatus(TaskStatus.PENDING);
    }

    @Test
    void getTasksByStatus_WithNoTasks_ReturnsEmptyList() {
        when(taskDataStore.findByStatus(TaskStatus.COMPLETED)).thenReturn(Collections.emptyList());

        List<TaskResponse> responses = taskService.getTasksByStatus(TaskStatus.COMPLETED);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(taskDataStore, times(1)).findByStatus(TaskStatus.COMPLETED);
    }

    // ========== GET ALL TASKS TESTS ==========

    @Test
    void getAllTasks_WithExistingTasks_ReturnsAllTasks() {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setCreatedAt(LocalDateTime.now());

        List<Task> tasks = Arrays.asList(task, task2);
        when(taskDataStore.findAll()).thenReturn(tasks);

        List<TaskResponse> responses = taskService.getAllTasks();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(taskDataStore, times(1)).findAll();
    }

    @Test
    void getAllTasks_WithNoTasks_ReturnsEmptyList() {
        when(taskDataStore.findAll()).thenReturn(Collections.emptyList());

        List<TaskResponse> responses = taskService.getAllTasks();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(taskDataStore, times(1)).findAll();
    }
}
