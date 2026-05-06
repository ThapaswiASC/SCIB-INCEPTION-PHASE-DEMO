package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.exceptions.*;
import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskManagementController.class)
class TaskManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    // ========== updateTaskStatus Tests ==========

    @Test
    void updateTaskStatus_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String taskId = "1";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus("IN_PROGRESS");
        request.setColumnId("in-progress");

        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setTaskId(taskId);
        response.setStatus("IN_PROGRESS");
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class));
    }

    @Test
    void updateTaskStatus_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String taskId = "999";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus("IN_PROGRESS");
        request.setColumnId("in-progress");

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class)))
            .thenThrow(new TaskNotFoundException(taskId));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateTaskStatus_InvalidStatusTransition_ReturnsBadRequest() throws Exception {
        // Arrange
        String taskId = "1";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus("DONE");
        request.setColumnId("done");

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class)))
            .thenThrow(new InvalidStatusTransitionException("TO_DO", "DONE"));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("INVALID_STATUS_TRANSITION"));
    }

    @Test
    void updateTaskStatus_MissingStatus_ReturnsBadRequest() throws Exception {
        // Arrange
        String taskId = "1";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setColumnId("in-progress");
        // status is null

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void updateTaskStatus_MissingColumnId_ReturnsBadRequest() throws Exception {
        // Arrange
        String taskId = "1";
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus("IN_PROGRESS");
        // columnId is null

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    // ========== getTaskDetails Tests ==========

    @Test
    void getTaskDetails_ValidTaskId_ReturnsOk() throws Exception {
        // Arrange
        String taskId = "1";
        TaskDetailsResponse response = new TaskDetailsResponse();
        response.setTaskId(taskId);
        response.setTitle("Test Task");
        response.setStatus("TO_DO");
        response.setColumnId("to-do");

        when(taskService.getTaskDetails(taskId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/{taskId}", taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.status").value("TO_DO"))
            .andExpect(jsonPath("$.columnId").value("to-do"));

        verify(taskService, times(1)).getTaskDetails(taskId);
    }

    @Test
    void getTaskDetails_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String taskId = "999";
        when(taskService.getTaskDetails(taskId))
            .thenThrow(new TaskNotFoundException(taskId));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/{taskId}", taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));
    }

    // ========== createTask Tests ==========

    @Test
    void createTask_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New Task");
        request.setDescription("Task description");
        request.setUserId(1L);
        request.setPriority("HIGH");
        request.setDueDate(LocalDateTime.now().plusDays(7));

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setUserId(request.getUserId());
        response.setStatus("PENDING");
        response.setPriority(request.getPriority());
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("New Task"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setUserId(1L);
        request.setPriority("HIGH");
        // title is null

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTask_TaskLimitExceeded_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New Task");
        request.setUserId(1L);
        request.setPriority("HIGH");

        when(taskService.createTask(any(TaskCreateRequest.class)))
            .thenThrow(new TaskLimitExceededException("User 1 has reached the maximum task limit of 10000"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("TASK_LIMIT_EXCEEDED"));
    }

    // ========== getUserTasks Tests ==========

    @Test
    void getUserTasks_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 1L;
        TaskResponse task1 = new TaskResponse();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setUserId(userId);

        TaskResponse task2 = new TaskResponse();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setUserId(userId);

        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(Arrays.asList(task1, task2));
        response.setNumber(0);
        response.setSize(50);
        response.setTotalElements(2L);
        response.setTotalPages(1);

        when(taskService.getUserTasks(eq(userId), any(Pageable.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(50))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));

        verify(taskService, times(1)).getUserTasks(eq(userId), any(Pageable.class));
    }

    @Test
    void getUserTasks_WithCustomPagination_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 1L;
        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(Collections.emptyList());
        response.setNumber(2);
        response.setSize(10);
        response.setTotalElements(25L);
        response.setTotalPages(3);

        when(taskService.getUserTasks(eq(userId), any(Pageable.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks", userId)
                .param("page", "2")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(2))
            .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getUserTasks_EmptyResult_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 999L;
        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(Collections.emptyList());
        response.setNumber(0);
        response.setSize(50);
        response.setTotalElements(0L);
        response.setTotalPages(0);

        when(taskService.getUserTasks(eq(userId), any(Pageable.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(0))
            .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ========== getTaskCount Tests ==========

    @Test
    void getTaskCount_ValidUserId_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 1L;
        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(42L);

        when(taskService.getTaskCount(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks/count", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.taskCount").value(42));

        verify(taskService, times(1)).getTaskCount(userId);
    }

    @Test
    void getTaskCount_ZeroTasks_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 999L;
        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(0L);

        when(taskService.getTaskCount(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks/count", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskCount").value(0));
    }

    // ========== updateTask Tests ==========

    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated description");
        request.setStatus("IN_PROGRESS");
        request.setPriority("URGENT");

        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setStatus(request.getStatus());
        response.setPriority(request.getPriority());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.updateTask(eq(taskId), any(TaskUpdateRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/update", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.priority").value("URGENT"));

        verify(taskService, times(1)).updateTask(eq(taskId), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long taskId = 999L;
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Title");

        when(taskService.updateTask(eq(taskId), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException(taskId));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/update", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));
    }

    @Test
    void updateTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("a".repeat(256)); // exceeds 255 character limit

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/update", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    // ========== deleteTask Tests ==========

    @Test
    void deleteTask_ValidTaskId_ReturnsNoContent() throws Exception {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/{taskId}/delete", taskId))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long taskId = 999L;
        doThrow(new TaskNotFoundException(taskId)).when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/{taskId}/delete", taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));
    }

    // ========== bulkCreateTasks Tests ==========

    @Test
    void bulkCreateTasks_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest();
        request1.setTitle("Task 1");
        request1.setUserId(1L);
        request1.setPriority("HIGH");

        TaskCreateRequest request2 = new TaskCreateRequest();
        request2.setTitle("Task 2");
        request2.setUserId(1L);
        request2.setPriority("MEDIUM");

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        TaskResponse response1 = new TaskResponse();
        response1.setId(1L);
        response1.setTitle("Task 1");

        TaskResponse response2 = new TaskResponse();
        response2.setId(2L);
        response2.setTitle("Task 2");

        BulkTaskResponse bulkResponse = new BulkTaskResponse();
        bulkResponse.setCreatedTasks(Arrays.asList(response1, response2));
        bulkResponse.setTotalCreated(2);
        bulkResponse.setFailures(Collections.emptyList());

        when(taskService.bulkCreateTasks(anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(2))
            .andExpect(jsonPath("$.createdTasks.length()").value(2))
            .andExpect(jsonPath("$.failures.length()").value(0));

        verify(taskService, times(1)).bulkCreateTasks(anyList());
    }

    @Test
    void bulkCreateTasks_PartialSuccess_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequest request1 = new TaskCreateRequest();
        request1.setTitle("Task 1");
        request1.setUserId(1L);
        request1.setPriority("HIGH");

        TaskCreateRequest request2 = new TaskCreateRequest();
        request2.setTitle("Task 2");
        request2.setUserId(1L);
        request2.setPriority("MEDIUM");

        List<TaskCreateRequest> requests = Arrays.asList(request1, request2);

        TaskResponse response1 = new TaskResponse();
        response1.setId(1L);
        response1.setTitle("Task 1");

        BulkTaskResponse.FailureInfo error = new BulkTaskResponse.FailureInfo();
        error.setIndex(1);
        error.setError("Task limit exceeded");

        BulkTaskResponse bulkResponse = new BulkTaskResponse();
        bulkResponse.setCreatedTasks(Collections.singletonList(response1));
        bulkResponse.setTotalCreated(1);
        bulkResponse.setFailures(Collections.singletonList(error));

        when(taskService.bulkCreateTasks(anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalCreated").value(1))
            .andExpect(jsonPath("$.createdTasks.length()").value(1))
            .andExpect(jsonPath("$.failures.length()").value(1))
            .andExpect(jsonPath("$.failures[0].index").value(1));
    }

    @Test
    void bulkCreateTasks_ExceedsLimit_ReturnsBadRequest() throws Exception {
        // Arrange
        List<TaskCreateRequest> requests = Collections.nCopies(101, new TaskCreateRequest());

        when(taskService.bulkCreateTasks(anyList()))
            .thenThrow(new TaskLimitExceededException("Bulk creation limited to 100 tasks at a time"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("TASK_LIMIT_EXCEEDED"));
    }
}