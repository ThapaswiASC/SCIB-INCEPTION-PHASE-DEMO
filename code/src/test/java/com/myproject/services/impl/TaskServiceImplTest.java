package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.datastores.UserDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.models.entities.User;
import com.myproject.services.interfaces.ColumnService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private UserDataStore userDataStore;

    @Mock
    private ColumnService columnService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void updateTaskStatus_ValidTransition_ReturnsResponse() {
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setStatus("TO_DO");
        task.setColumnId("to-do");

        TaskStatusUpdateRequest request = TaskStatusUpdateRequest.builder()
                .status(TaskStatusUpdateRequest.TaskStatus.IN_PROGRESS)
                .columnId("in-progress")
                .build();

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setStatus("IN_PROGRESS");
        updatedTask.setColumnId("in-progress");
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);
        doNothing().when(columnService).updateColumnCounts(anyString(), anyString());

        TaskStatusUpdateResponse response = taskService.updateTaskStatus(taskId, request);

        assertNotNull(response);
        assertEquals("1", response.getTaskId());
        assertEquals("IN_PROGRESS", response.getStatus());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void getTaskDetails_ValidTaskId_ReturnsResponse() {
        String taskId = "1";
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus("TO_DO");
        task.setColumnId("to-do");

        when(taskDataStore.findById(1L)).thenReturn(Optional.of(task));

        TaskDetailsResponse response = taskService.getTaskDetails(taskId);

        assertNotNull(response);
        assertEquals("1", response.getTaskId());
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void createTask_ValidRequest_ReturnsResponse() {
        TaskCreateRequest request = TaskCreateRequest.builder()
                .title("New Task")
                .description("Description")
                .userId(1L)
                .priority(TaskCreateRequest.TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();

        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .taskCount(0L)
                .build();

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(request.getTitle());
        savedTask.setDescription(request.getDescription());
        savedTask.setUserId(request.getUserId());
        savedTask.setStatus("PENDING");
        savedTask.setPriority(request.getPriority().name());
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(userDataStore.findById(1L)).thenReturn(Optional.of(user));
        when(taskDataStore.countByUserId(1L)).thenReturn(100L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask);
        doNothing().when(userDataStore).incrementTaskCount(1L);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Task", response.getTitle());
    }

    @Test
    void getUserTasks_ValidRequest_ReturnsPagedResponse() {
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

        Pageable pageable = PageRequest.of(0, 50);
        PagedTaskResponse response = taskService.getUserTasks(userId, pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(2L, response.getTotalElements());
    }

    @Test
    void getTaskCount_ValidUserId_ReturnsCount() {
        Long userId = 1L;
        when(taskDataStore.countByUserId(userId)).thenReturn(42L);

        TaskCountResponse response = taskService.getTaskCount(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(42L, response.getTaskCount());
    }

    @Test
    void updateTask_ValidRequest_ReturnsResponse() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Old Title");
        task.setDescription("Old Description");

        TaskUpdateRequest request = TaskUpdateRequest.builder()
                .title("New Title")
                .description("New Description")
                .status(TaskUpdateRequest.TaskStatus.IN_PROGRESS)
                .priority(TaskUpdateRequest.TaskPriority.URGENT)
                .build();

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle(request.getTitle());
        updatedTask.setDescription(request.getDescription());
        updatedTask.setStatus(request.getStatus().name());
        updatedTask.setPriority(request.getPriority().name());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        when(taskDataStore.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask(taskId, request);

        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("New Title", response.getTitle());
    }

    @Test
    void deleteTask_ValidTaskId_DeletesTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setUserId(1L);

        when(taskDataStore.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskDataStore).deleteById(taskId);
        doNothing().when(userDataStore).decrementTaskCount(1L);

        taskService.deleteTask(taskId);

        verify(taskDataStore).findById(taskId);
        verify(taskDataStore).deleteById(taskId);
        verify(userDataStore).decrementTaskCount(1L);
    }

    @Test
    void bulkCreateTasks_ValidRequests_ReturnsResponse() {
        TaskCreateRequest request1 = TaskCreateRequest.builder()
                .title("Task 1")
                .userId(1L)
                .priority(TaskCreateRequest.TaskPriority.HIGH)
                .build();

        TaskCreateRequest request2 = TaskCreateRequest.builder()
                .title("Task 2")
                .userId(1L)
                .priority(TaskCreateRequest.TaskPriority.MEDIUM)
                .build();

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .taskCount(0L)
                .build();

        Task savedTask1 = new Task();
        savedTask1.setId(1L);
        savedTask1.setTitle("Task 1");
        savedTask1.setStatus("PENDING");

        Task savedTask2 = new Task();
        savedTask2.setId(2L);
        savedTask2.setTitle("Task 2");
        savedTask2.setStatus("PENDING");

        when(userDataStore.findById(1L)).thenReturn(Optional.of(user));
        when(taskDataStore.countByUserId(1L)).thenReturn(0L);
        when(taskDataStore.save(any(Task.class))).thenReturn(savedTask1, savedTask2);
        doNothing().when(userDataStore).incrementTaskCount(1L);

        BulkTaskResponse response = taskService.bulkCreateTasks(requests);

        assertNotNull(response);
        assertEquals(2, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
    }
}