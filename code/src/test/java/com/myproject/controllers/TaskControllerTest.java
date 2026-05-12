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
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(1));

        TaskResponse response = new TaskResponse();
        response.setId(UUID.randomUUID());
        response.setTitle("Test Task");
        response.setDescription("Test Description");
        response.setUserId("user123");
        response.setPriority(TaskPriority.HIGH);
        response.setStatus(TaskStatus.PENDING);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setDueDate(LocalDateTime.now().plusDays(1));

        when(taskService.createTask(eq("user123"), any(TaskCreateRequest.class))).thenReturn(response);

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

        verify(taskService, times(1)).createTask(eq("user123"), any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setDescription("Test Description");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(1));

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
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle(longTitle);
        request.setDescription("Test Description");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(1));

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
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setDueDate(LocalDateTime.now().plusDays(1));

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
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(1));

        when(taskService.createTask(eq("user123"), any(TaskCreateRequest.class)))
            .thenThrow(new TaskLimitExceededException("User has reached the maximum limit of 10000 tasks"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("TASK_LIMIT_EXCEEDED"));

        verify(taskService, times(1)).createTask(eq("user123"), any(TaskCreateRequest.class));
    }

    // ========== GET /v1/users/{userId}/tasks - Get User Tasks ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskResponse task1 = new TaskResponse();
        task1.setId(UUID.randomUUID());
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUserId("user123");
        task1.setPriority(TaskPriority.HIGH);
        task1.setStatus(TaskStatus.PENDING);
        task1.setCreatedAt(LocalDateTime.now());
        task1.setUpdatedAt(LocalDateTime.now());

        TaskResponse task2 = new TaskResponse();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUserId("user123");
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        List<TaskResponse> tasks = Arrays.asList(task1, task2);

        when(taskService.getUserTasks(eq("user123"), anyInt(), anyInt(), anyString())).thenReturn(tasks);

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
        when(taskService.getUserTasks(eq("user123"), anyInt(), anyInt(), anyString())).thenReturn(Collections.emptyList());

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
        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setTitle("Task 1");
        response.setDescription("Description 1");
        response.setUserId("user123");
        response.setPriority(TaskPriority.HIGH);
        response.setStatus(TaskStatus.PENDING);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.getTaskById(any(UUID.class), eq("user123"))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/" + taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Task 1"))
            .andExpect(jsonPath("$.description").value("Description 1"));

        verify(taskService, times(1)).getTaskById(any(UUID.class), eq("user123"));
    }

    @Test
    void getTaskById_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        when(taskService.getTaskById(any(UUID.class), eq("user123")))
            .thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/" + taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).getTaskById(any(UUID.class), eq("user123"));
    }

    // ========== PUT /v1/tasks/{taskId} - Update Task ==========

    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");
        request.setPriority(TaskPriority.HIGH);
        request.setStatus(TaskStatus.COMPLETED);
        request.setDueDate(LocalDateTime.now().plusDays(2));

        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setTitle("Updated Task");
        response.setDescription("Updated Description");
        response.setUserId("user123");
        response.setPriority(TaskPriority.HIGH);
        response.setStatus(TaskStatus.COMPLETED);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setDueDate(LocalDateTime.now().plusDays(2));

        when(taskService.updateTask(any(UUID.class), eq("user123"), any(TaskUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).updateTask(any(UUID.class), eq("user123"), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");
        request.setPriority(TaskPriority.HIGH);
        request.setStatus(TaskStatus.COMPLETED);

        when(taskService.updateTask(any(UUID.class), eq("user123"), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).updateTask(any(UUID.class), eq("user123"), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        String longTitle = "a".repeat(256);
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle(longTitle);
        request.setDescription("Updated Description");
        request.setPriority(TaskPriority.HIGH);
        request.setStatus(TaskStatus.COMPLETED);

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
        doNothing().when(taskService).deleteTask(any(UUID.class), eq("user123"));

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/" + taskId))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(any(UUID.class), eq("user123"));
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID taskId = UUID.randomUUID();
        doThrow(new TaskNotFoundException("Task not found"))
            .when(taskService).deleteTask(any(UUID.class), eq("user123"));

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/" + taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).deleteTask(any(UUID.class), eq("user123"));
    }

    // ========== POST /v1/tasks/bulk - Bulk Create Tasks ==========

    @Test
    void bulkCreateTasks_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest();
        request1.setTitle("Task 1");
        request1.setDescription("Description 1");
        request1.setPriority(TaskPriority.HIGH);

        TaskCreateRequest request2 = new TaskCreateRequest();
        request2.setTitle("Task 2");
        request2.setDescription("Description 2");
        request2.setPriority(TaskPriority.MEDIUM);

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        TaskResponse response1 = new TaskResponse();
        response1.setId(UUID.randomUUID());
        response1.setTitle("Task 1");
        response1.setDescription("Description 1");
        response1.setUserId("user123");
        response1.setPriority(TaskPriority.HIGH);
        response1.setStatus(TaskStatus.PENDING);
        response1.setCreatedAt(LocalDateTime.now());
        response1.setUpdatedAt(LocalDateTime.now());

        TaskResponse response2 = new TaskResponse();
        response2.setId(UUID.randomUUID());
        response2.setTitle("Task 2");
        response2.setDescription("Description 2");
        response2.setUserId("user123");
        response2.setPriority(TaskPriority.MEDIUM);
        response2.setStatus(TaskStatus.PENDING);
        response2.setCreatedAt(LocalDateTime.now());
        response2.setUpdatedAt(LocalDateTime.now());

        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            2, 0, Arrays.asList(response1, response2), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(eq("user123"), anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(2));

        verify(taskService, times(1)).bulkCreateTasks(eq("user123"), anyList());
    }

    @Test
    void bulkCreateTasks_EmptyList_ReturnsCreated() throws Exception {
        // Arrange
        List<TaskCreateRequest> requests = Collections.emptyList();
        BulkTaskResponse bulkResponse = new BulkTaskResponse(
            0, 0, Collections.emptyList(), Collections.emptyList()
        );

        when(taskService.bulkCreateTasks(eq("user123"), anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(0));

        verify(taskService, times(1)).bulkCreateTasks(eq("user123"), anyList());
    }
}