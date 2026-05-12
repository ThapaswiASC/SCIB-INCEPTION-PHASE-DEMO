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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
            1L,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        TaskResponse response = new TaskResponse(
            1L,
            "Test Task",
            "Test Description",
            1L,
            Priority.HIGH,
            TaskStatus.PENDING,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        );

        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskService, times(1)).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            null,
            "Test Description",
            1L,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        String longTitle = "a".repeat(256);
        TaskCreateRequest request = new TaskCreateRequest(
            longTitle,
            "Test Description",
            1L,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            null,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingPriority_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            1L,
            null,
            LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_TaskLimitExceeded_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
            "Test Task",
            "Test Description",
            1L,
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(taskService.createTask(any(TaskCreateRequest.class)))
            .thenThrow(new TaskLimitExceededException("User 1 has reached the maximum limit of 10000 tasks"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("TASK_LIMIT_EXCEEDED"));

        verify(taskService, times(1)).createTask(any(TaskCreateRequest.class));
    }

    // ========== GET /v1/users/{userId}/tasks - Get User Tasks ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskResponse task1 = new TaskResponse(
            1L, "Task 1", "Description 1", 1L, Priority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        TaskResponse task2 = new TaskResponse(
            2L, "Task 2", "Description 2", 1L, Priority.MEDIUM, TaskStatus.IN_PROGRESS,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        List<TaskResponse> tasks = Arrays.asList(task1, task2);

        when(taskService.getUserTasks(eq(1L), anyInt(), anyInt())).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("Task 2"));

        verify(taskService, times(1)).getUserTasks(1L, 0, 20);
    }

    @Test
    void getUserTasks_DefaultPagination_ReturnsOk() throws Exception {
        // Arrange
        when(taskService.getUserTasks(eq(1L), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getUserTasks(1L, 0, 20);
    }

    @Test
    void getUserTasks_InvalidPageNumber_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks")
                .param("page", "-1")
                .param("size", "20"))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).getUserTasks(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserTasks_InvalidPageSize_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks")
                .param("page", "0")
                .param("size", "0"))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).getUserTasks(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserTasks_PageSizeExceedsMax_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks")
                .param("page", "0")
                .param("size", "101"))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).getUserTasks(anyLong(), anyInt(), anyInt());
    }

    // ========== GET /v1/users/{userId}/tasks/count - Get Task Count ==========

    @Test
    void getTaskCount_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskCountResponse response = new TaskCountResponse(1L, 42);
        when(taskService.getTaskCount(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.taskCount").value(42));

        verify(taskService, times(1)).getTaskCount(1L);
    }

    // ========== GET /v1/tasks/{taskId} - Get Task by ID ==========

    @Test
    void getTaskById_ValidId_ReturnsOk() throws Exception {
        // Arrange
        TaskResponse response = new TaskResponse(
            1L, "Task 1", "Description 1", 1L, Priority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        when(taskService.getTaskById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Task 1"))
            .andExpect(jsonPath("$.description").value("Description 1"));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void getTaskById_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(999L))
            .thenThrow(new TaskNotFoundException("Task with ID 999 not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).getTaskById(999L);
    }

    // ========== PUT /v1/tasks/{taskId} - Update Task ==========

    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest(
            "Updated Task",
            "Updated Description",
            Priority.CRITICAL,
            TaskStatus.COMPLETED,
            LocalDateTime.now().plusDays(2)
        );

        TaskResponse response = new TaskResponse(
            1L, "Updated Task", "Updated Description", 1L, Priority.CRITICAL, TaskStatus.COMPLETED,
            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)
        );

        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest(
            "Updated Task",
            "Updated Description",
            Priority.CRITICAL,
            TaskStatus.COMPLETED,
            null
        );

        when(taskService.updateTask(eq(999L), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException("Task with ID 999 not found"));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).updateTask(eq(999L), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        String longTitle = "a".repeat(256);
        TaskUpdateRequest request = new TaskUpdateRequest(
            longTitle,
            "Updated Description",
            Priority.CRITICAL,
            TaskStatus.COMPLETED,
            null
        );

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(anyLong(), any(TaskUpdateRequest.class));
    }

    // ========== DELETE /v1/tasks/{taskId} - Delete Task ==========

    @Test
    void deleteTask_ValidId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(1L);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new TaskNotFoundException("Task with ID 999 not found"))
            .when(taskService).deleteTask(999L);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).deleteTask(999L);
    }

    // ========== POST /v1/tasks/bulk - Bulk Create Tasks ==========

    @Test
    void bulkCreateTasks_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest(
            "Task 1", "Description 1", 1L, Priority.HIGH, null
        );
        TaskCreateRequest request2 = new TaskCreateRequest(
            "Task 2", "Description 2", 1L, Priority.MEDIUM, null
        );
        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        TaskResponse response1 = new TaskResponse(
            1L, "Task 1", "Description 1", 1L, Priority.HIGH, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );
        TaskResponse response2 = new TaskResponse(
            2L, "Task 2", "Description 2", 1L, Priority.MEDIUM, TaskStatus.PENDING,
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            2, 0, Arrays.asList(response1, response2), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(any())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.successCount").value(2))
            .andExpect(jsonPath("$.failureCount").value(0))
            .andExpect(jsonPath("$.tasks", hasSize(2)));

        verify(taskService, times(1)).bulkCreateTasks(any());
    }

    @Test
    void bulkCreateTasks_ExceedsMaxSize_ThrowsException() throws Exception {
        // Arrange - Create 101 requests
        List<TaskCreateRequest> requests = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) {
            requests.add(new TaskCreateRequest(
                "Task " + i, "Description " + i, 1L, Priority.LOW, null
            ));
        }

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isInternalServerError());

        verify(taskService, never()).bulkCreateTasks(any());
    }

    @Test
    void bulkCreateTasks_EmptyList_ReturnsCreated() throws Exception {
        // Arrange
        List<TaskCreateRequest> requests = Collections.emptyList();
        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            0, 0, Collections.emptyList(), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(any())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.successCount").value(0))
            .andExpect(jsonPath("$.failureCount").value(0));

        verify(taskService, times(1)).bulkCreateTasks(any());
    }
}