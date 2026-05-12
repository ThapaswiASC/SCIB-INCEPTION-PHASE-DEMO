package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.exceptions.*;
import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    // ========== POST /v1/tasks - Create Task ==========

    @Test
    void createTask_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            TaskPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        TaskResponse response = new TaskResponse(
            UUID.randomUUID(),
            "Test Task",
            "Test Description",
            "user123",
            TaskPriority.HIGH,
            TaskStatus.PENDING,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        );

        when(taskService.createTask(anyString(), any(TaskCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.userId").value("user123"))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskService, times(1)).createTask(anyString(), any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            null,
            "Test Description",
            null,
            TaskPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(anyString(), any(TaskCreateRequest.class));
    }

    @Test
    void createTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        String longTitle = "a".repeat(256);
        TaskCreateRequest request = new TaskCreateRequest(
            longTitle,
            "Test Description",
            null,
            TaskPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(anyString(), any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingPriority_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            null,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(anyString(), any(TaskCreateRequest.class));
    }

    @Test
    void createTask_TaskLimitExceeded_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            TaskPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(taskService.createTask(anyString(), any(TaskCreateRequest.class)))
            .thenThrow(new TaskLimitExceededException("User has reached the maximum limit of 10000 tasks"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("TASK_LIMIT_EXCEEDED"));

        verify(taskService, times(1)).createTask(anyString(), any(TaskCreateRequest.class));
    }

    // ========== GET /v1/users/{userId}/tasks - Get User Tasks ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskResponse task1 = new TaskResponse(
            UUID.randomUUID(), "Task 1", "Description 1", "user123", TaskPriority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        TaskResponse task2 = new TaskResponse(
            UUID.randomUUID(), "Task 2", "Description 2", "user123", TaskPriority.MEDIUM, TaskStatus.IN_PROGRESS,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        List<TaskResponse> tasks = Arrays.asList(task1, task2);

        when(taskService.getUserTasks(anyString(), anyInt(), anyInt(), anyString())).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/v1/users/user123/tasks")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].title").value("Task 2"));

        verify(taskService, times(1)).getUserTasks(eq("user123"), eq(0), eq(20), anyString());
    }

    @Test
    void getUserTasks_DefaultPagination_ReturnsOk() throws Exception {
        // Arrange
        when(taskService.getUserTasks(anyString(), anyInt(), anyInt(), anyString())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/v1/users/user123/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getUserTasks(eq("user123"), eq(0), eq(20), anyString());
    }

    // ========== GET /v1/users/{userId}/tasks/count - Get Task Count ==========

    @Test
    void getTaskCount_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskCountResponse response = new TaskCountResponse("user123", 42);
        when(taskService.getTaskCount("user123")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/user123/tasks/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("user123"))
            .andExpect(jsonPath("$.taskCount").value(42));

        verify(taskService, times(1)).getTaskCount("user123");
    }

    // ========== GET /v1/tasks/{taskId} - Get Task by ID ==========

    @Test
    void getTaskById_ValidId_ReturnsOk() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskResponse response = new TaskResponse(
            taskId, "Task 1", "Description 1", "user123", TaskPriority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        when(taskService.getTaskById(any(UUID.class), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/" + taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Task 1"))
            .andExpect(jsonPath("$.description").value("Description 1"));

        verify(taskService, times(1)).getTaskById(any(UUID.class), anyString());
    }

    @Test
    void getTaskById_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        when(taskService.getTaskById(any(UUID.class), anyString()))
            .thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/" + taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).getTaskById(any(UUID.class), anyString());
    }

    // ========== PUT /v1/tasks/{taskId} - Update Task ==========

    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskUpdateRequest request = new TaskUpdateRequest(
            "Updated Task",
            "Updated Description",
            TaskPriority.HIGH,
            TaskStatus.COMPLETED,
            LocalDateTime.now().plusDays(2)
        );

        TaskResponse response = new TaskResponse(
            taskId, "Updated Task", "Updated Description", "user123", TaskPriority.HIGH, TaskStatus.COMPLETED,
            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)
        );

        when(taskService.updateTask(any(UUID.class), anyString(), any(TaskUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).updateTask(any(UUID.class), anyString(), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskUpdateRequest request = new TaskUpdateRequest(
            "Updated Task",
            "Updated Description",
            TaskPriority.HIGH,
            TaskStatus.COMPLETED,
            null
        );

        when(taskService.updateTask(any(UUID.class), anyString(), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).updateTask(any(UUID.class), anyString(), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        String longTitle = "a".repeat(256);
        TaskUpdateRequest request = new TaskUpdateRequest(
            longTitle,
            "Updated Description",
            TaskPriority.HIGH,
            TaskStatus.COMPLETED,
            null
        );

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(any(UUID.class), anyString(), any(TaskUpdateRequest.class));
    }

    // ========== DELETE /v1/tasks/{taskId} - Delete Task ==========

    @Test
    void deleteTask_ValidId_ReturnsNoContent() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        doNothing().when(taskService).deleteTask(any(UUID.class), anyString());

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/" + taskId))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(any(UUID.class), anyString());
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        doThrow(new TaskNotFoundException("Task not found"))
            .when(taskService).deleteTask(any(UUID.class), anyString());

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/" + taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).deleteTask(any(UUID.class), anyString());
    }

    // ========== POST /v1/tasks/bulk - Bulk Create Tasks ==========

    @Test
    void bulkCreateTasks_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", null, TaskPriority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", null, TaskPriority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        TaskResponse response1 = new TaskResponse(
            UUID.randomUUID(), "Task 1", "Description 1", "user123", TaskPriority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        TaskResponse response2 = new TaskResponse(
            UUID.randomUUID(), "Task 2", "Description 2", "user123", TaskPriority.MEDIUM, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            2, 0, Arrays.asList(response1, response2), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(anyString(), anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(2));

        verify(taskService, times(1)).bulkCreateTasks(anyString(), anyList());
    }

    @Test
    void bulkCreateTasks_EmptyList_ReturnsCreated() throws Exception {
        // Arrange
        List<TaskCreateRequest> requests = Collections.emptyList();
        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            0, 0, Collections.emptyList(), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(anyString(), anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(0));

        verify(taskService, times(1)).bulkCreateTasks(anyString(), anyList());
    }
}