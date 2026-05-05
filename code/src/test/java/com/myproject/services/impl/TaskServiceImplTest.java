package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.ColumnService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDataStore taskDataStore;

    @Mock
    private ColumnService columnService;

    @InjectMocks
    private TaskServiceImpl taskService;

    // ========== updateTaskStatus Tests ==========

    @Test
    void updateTaskStatus_ValidTransition_ReturnsResponse() {
        // Arrange
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TO_DO);
        task.setColumnId("to-do");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setColumnId("in-progress");

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setColumnId("in-progress");
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);
        doNothing().when(columnService).updateColumnCounts(anyString(), anyString());

        // Act
        UpdateTaskStatusResponse response = taskService.updateTaskStatus(taskId, request);

        // Assert
        assertNotNull(response);
        assertEquals("1", response.getTaskId());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
        assertNotNull(response.getUpdatedAt());
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore).save(any(Task.class));
        verify(columnService).updateColumnCounts("to-do", "in-progress");
    }

    @Test
    void updateTaskStatus_TaskNotFound_ThrowsException() {
        // Arrange
        String taskId = "999";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setColumnId("in-progress");

        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTaskStatus(taskId, request);
        });
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_InvalidTransition_ThrowsException() {
        // Arrange
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TO_DO);
        task.setColumnId("to-do");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);
        request.setColumnId("done");

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            taskService.updateTaskStatus(taskId, request);
        });
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_SameColumn_DoesNotUpdateColumnCounts() {
        // Arrange
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setColumnId("in-progress");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.TO_DO);
        request.setColumnId("in-progress");

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setStatus(TaskStatus.TO_DO);
        updatedTask.setColumnId("in-progress");
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        UpdateTaskStatusResponse response = taskService.updateTaskStatus(taskId, request);

        // Assert
        assertNotNull(response);
        verify(columnService, never()).updateColumnCounts(anyString(), anyString());
    }

    // ========== getTaskDetails Tests ==========

    @Test
    void getTaskDetails_ValidTaskId_ReturnsResponse() {
        // Arrange
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(TaskStatus.TO_DO);
        task.setColumnId("to-do");

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        TaskDetailsResponse response = taskService.getTaskDetails(taskId);

        // Assert
        assertNotNull(response);
        assertEquals("1", response.getTaskId());
        assertEquals("Test Task", response.getTitle());
        assertEquals(TaskStatus.TO_DO, response.getStatus());
        assertEquals("to-do", response.getColumnId());
        verify(taskDataStore).findById(taskId);
    }

    @Test
    void getTaskDetails_TaskNotFound_ThrowsException() {
        // Arrange
        String taskId = "999";
        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskDetails(taskId);
        });
        verify(taskDataStore).findById(taskId);
    }

    // ========== createTask Tests ==========

    @Test
    void createTask_ValidRequest_ReturnsResponse() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setUserId(1L);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(7));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(request.getTitle());
        savedTask.setDescription(request.getDescription());
        savedTask.setUserId(request.getUserId());
        savedTask.setStatus(TaskStatus.PENDING);
        savedTask.setPriority(request.getPriority());
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());
        savedTask.setVersion(0L);

        when(taskDataStore.countByUserId(1L)).thenReturn(100L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Task", response.getTitle());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        verify(taskDataStore).countByUserId(1L);
        verify(taskDataStore).save(any(Task.class));
    }

    @Test
    void createTask_TaskLimitExceeded_ThrowsException() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New Task");
        request.setUserId(1L);
        request.setPriority(TaskPriority.HIGH);

        when(taskDataStore.countByUserId(1L)).thenReturn(10000L);

        // Act & Assert
        assertThrows(TaskLimitExceededException.class, () -> {
            taskService.createTask(request);
        });
        verify(taskDataStore).countByUserId(1L);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    // ========== getUserTasks Tests ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsPagedResponse() {
        // Arrange
        Long userId = 1L;
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setUserId(userId);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setUserId(userId);

        when(taskDataStore.findByUserId(userId, 0, 50)).thenReturn(Arrays.asList(task1, task2));
        when(taskDataStore.countByUserId(userId)).thenReturn(2L);

        // Act
        PagedTaskResponse response = taskService.getUserTasks(userId, 0, 50, "createdAt,desc");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(50, response.getSize());
        assertEquals(2L, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.getLast());
        verify(taskDataStore).findByUserId(userId, 0, 50);
        verify(taskDataStore).countByUserId(userId);
    }

    @Test
    void getUserTasks_ExceedsMaxSize_LimitsToHundred() {
        // Arrange
        Long userId = 1L;
        when(taskDataStore.findByUserId(userId, 0, 100)).thenReturn(Collections.emptyList());
        when(taskDataStore.countByUserId(userId)).thenReturn(0L);

        // Act
        PagedTaskResponse response = taskService.getUserTasks(userId, 0, 150, "createdAt,desc");

        // Assert
        assertNotNull(response);
        assertEquals(100, response.getSize());
        verify(taskDataStore).findByUserId(userId, 0, 100);
    }

    @Test
    void getUserTasks_EmptyResult_ReturnsEmptyPage() {
        // Arrange
        Long userId = 999L;
        when(taskDataStore.findByUserId(userId, 0, 50)).thenReturn(Collections.emptyList());
        when(taskDataStore.countByUserId(userId)).thenReturn(0L);

        // Act
        PagedTaskResponse response = taskService.getUserTasks(userId, 0, 50, "createdAt,desc");

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
    }

    // ========== getTaskCount Tests ==========

    @Test
    void getTaskCount_ValidUserId_ReturnsCount() {
        // Arrange
        Long userId = 1L;
        when(taskDataStore.countByUserId(userId)).thenReturn(42L);

        // Act
        TaskCountResponse response = taskService.getTaskCount(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(42L, response.getTaskCount());
        verify(taskDataStore).countByUserId(userId);
    }

    @Test
    void getTaskCount_ExceedsMaxLimit_ReturnsMaxLimit() {
        // Arrange
        Long userId = 1L;
        when(taskDataStore.countByUserId(userId)).thenReturn(15000L);

        // Act
        TaskCountResponse response = taskService.getTaskCount(userId);

        // Assert
        assertNotNull(response);
        assertEquals(10000L, response.getTaskCount());
    }

    // ========== updateTask Tests ==========

    @Test
    void updateTask_ValidRequest_ReturnsResponse() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Old Title");
        task.setDescription("Old Description");

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setPriority(TaskPriority.URGENT);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle(request.getTitle());
        updatedTask.setDescription(request.getDescription());
        updatedTask.setStatus(request.getStatus());
        updatedTask.setPriority(request.getPriority());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskResponse response = taskService.updateTask(taskId, request);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("New Title", response.getTitle());
        assertEquals("New Description", response.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
        assertEquals(TaskPriority.URGENT, response.getPriority());
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore).save(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound_ThrowsException() {
        // Arrange
        Long taskId = 999L;
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New Title");

        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(taskId, request);
        });
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void updateTask_PartialUpdate_UpdatesOnlyProvidedFields() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Original Title");
        task.setDescription("Original Description");
        task.setStatus(TaskStatus.TO_DO);
        task.setPriority(TaskPriority.LOW);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Title");
        // Other fields are null

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Original Description");
        updatedTask.setStatus(TaskStatus.TO_DO);
        updatedTask.setPriority(TaskPriority.LOW);

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskResponse response = taskService.updateTask(taskId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        verify(taskDataStore).save(any(Task.class));
    }

    // ========== deleteTask Tests ==========

    @Test
    void deleteTask_ValidTaskId_DeletesTask() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskDataStore).deleteById(taskId);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore).deleteById(taskId);
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsException() {
        // Arrange
        Long taskId = 999L;
        when(taskDataStore.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(taskId);
        });
        verify(taskDataStore).findById(taskId);
        verify(taskDataStore, never()).deleteById(anyLong());
    }

    // ========== bulkCreateTasks Tests ==========

    @Test
    void bulkCreateTasks_ValidRequests_ReturnsResponse() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest();
        request1.setTitle("Task 1");
        request1.setUserId(1L);
        request1.setPriority(TaskPriority.HIGH);

        TaskCreateRequest request2 = new TaskCreateRequest();
        request2.setTitle("Task 2");
        request2.setUserId(1L);
        request2.setPriority(TaskPriority.MEDIUM);

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(1L);
        savedTask1.setTitle("Task 1");
        savedTask1.setStatus(TaskStatus.PENDING);

        Task savedTask2 = new Task();
        savedTask2.setId(2L);
        savedTask2.setTitle("Task 2");
        savedTask2.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId(1L)).thenReturn(0L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask1, savedTask2);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertEquals(2, response.getCreatedTasks().size());
        assertTrue(response.getErrors().isEmpty());
        verify(taskDataStore, times(2)).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_ExceedsLimit_ThrowsException() {
        // Arrange
        List<TaskCreateRequest> requests = Collections.nCopies(101, new TaskCreateRequest());

        // Act & Assert
        assertThrows(TaskLimitExceededException.class, () -> {
            taskService.bulkCreateTasks(requests);
        });
        verify(taskDataStore, never()).save(any(Task.class));
    }

    @Test
    void bulkCreateTasks_PartialFailure_ReturnsPartialResponse() {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest();
        request1.setTitle("Task 1");
        request1.setUserId(1L);
        request1.setPriority(TaskPriority.HIGH);

        TaskCreateRequest request2 = new TaskCreateRequest();
        request2.setTitle("Task 2");
        request2.setUserId(1L);
        request2.setPriority(TaskPriority.MEDIUM);

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        Task savedTask1 = new Task();
        savedTask1.setId(1L);
        savedTask1.setTitle("Task 1");
        savedTask1.setStatus(TaskStatus.PENDING);

        when(taskDataStore.countByUserId(1L)).thenReturn(0L).thenReturn(10000L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask1);

        // Act
        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals(1, response.getCreatedTasks().size());
        assertEquals(1, response.getErrors().size());
        assertEquals(1, response.getErrors().get(0).getIndex());
    }
}