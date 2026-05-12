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

import static org.mockito.ArgumentMatchers.*;
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

    // ========== updateTaskStatus Tests ==========

    @Test
    void updateTaskStatus_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskStatusUpdateRequest request = TaskStatusUpdateRequest.builder()
            .status(TaskStatusUpdateRequest.TaskStatus.IN_PROGRESS)
            .columnId("in-progress")
            .build();

        TaskStatusUpdateResponse response = TaskStatusUpdateResponse.builder()
            .taskId(taskId)
            .status("IN_PROGRESS")
            .updatedAt(LocalDateTime.now())
            .message("Task status updated successfully")
            .build();

        when(taskService.updateTaskStatus(eq(taskId), any(TaskStatusUpdateRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).updateTaskStatus(eq(taskId), any(TaskStatusUpdateRequest.class));
    }

    @Test
    void updateTaskStatus_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long taskId = 999L;
        TaskStatusUpdateRequest request = TaskStatusUpdateRequest.builder()
            .status(TaskStatusUpdateRequest.TaskStatus.IN_PROGRESS)
            .columnId("in-progress")
            .build();

        when(taskService.updateTaskStatus(eq(taskId), any(TaskStatusUpdateRequest.class)))
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
        Long taskId = 1L;
        TaskStatusUpdateRequest request = TaskStatusUpdateRequest.builder()
            .status(TaskStatusUpdateRequest.TaskStatus.DONE)
            .columnId("done")
            .build();

        when(taskService.updateTaskStatus(eq(taskId), any(TaskStatusUpdateRequest.class)))
            .thenThrow(new InvalidStatusTransitionException("TODO", "DONE"));

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
        Long taskId = 1L;
        String requestJson = "{"columnId":"in-progress"}";

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void updateTaskStatus_MissingColumnId_ReturnsBadRequest() throws Exception {
        // Arrange
        Long taskId = 1L;
        String requestJson = "{"status":"IN_PROGRESS"}";

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    // ========== getTaskById Tests ==========

    @Test
    void getTaskById_ValidTaskId_ReturnsOk() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskDetailsResponse response = TaskDetailsResponse.builder()
            .taskId(taskId)
            .title("Test Task")
            .status("TODO")
            .assignee("John Doe")
            .createdAt(LocalDateTime.now())
            .build();

        when(taskService.getTaskById(taskId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks/{taskId}", taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskById_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long taskId = 999L;
        when(taskService.getTaskById(taskId))
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
        TaskCreateRequest request = TaskCreateRequest.builder()
            .title("New Task")
            .description("Task description")
            .userId(1L)
            .priority(TaskCreateRequest.TaskPriority.HIGH)
            .dueDate(LocalDateTime.now().plusDays(7))
            .build();

        TaskResponse response = TaskResponse.builder()
            .id(1L)
            .title(request.getTitle())
            .description(request.getDescription())
            .userId(request.getUserId())
            .status("PENDING")
            .priority("HIGH")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

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
        String requestJson = "{"userId":1,"priority":"HIGH"}";

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTask_MissingUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestJson = "{"title":"New Task","priority":"HIGH"}";

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTask_TaskLimitExceeded_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequest request = TaskCreateRequest.builder()
            .title("New Task")
            .userId(1L)
            .priority(TaskCreateRequest.TaskPriority.HIGH)
            .build();

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
        TaskResponse task1 = TaskResponse.builder()
            .id(1L)
            .title("Task 1")
            .userId(userId)
            .build();

        TaskResponse task2 = TaskResponse.builder()
            .id(2L)
            .title("Task 2")
            .userId(userId)
            .build();

        PagedTaskResponse response = PagedTaskResponse.builder()
            .content(Arrays.asList(task1, task2))
            .page(0)
            .size(50)
            .totalElements(2L)
            .totalPages(1)
            .build();

        when(taskService.getUserTasks(eq(userId), eq(0), eq(50)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(50))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));

        verify(taskService, times(1)).getUserTasks(eq(userId), eq(0), eq(50));
    }

    @Test
    void getUserTasks_WithCustomPagination_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 1L;
        PagedTaskResponse response = PagedTaskResponse.builder()
            .content(Collections.emptyList())
            .page(2)
            .size(10)
            .totalElements(25L)
            .totalPages(3)
            .build();

        when(taskService.getUserTasks(eq(userId), eq(2), eq(10)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/tasks", userId)
                .param("page", "2")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getUserTasks_EmptyResult_ReturnsOk() throws Exception {
        // Arrange
        Long userId = 999L;
        PagedTaskResponse response = PagedTaskResponse.builder()
            .content(Collections.emptyList())
            .page(0)
            .size(50)
            .totalElements(0L)
            .totalPages(0)
            .build();

        when(taskService.getUserTasks(eq(userId), eq(0), eq(50)))
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
        TaskCountResponse response = TaskCountResponse.builder()
            .userId(userId)
            .taskCount(42L)
            .build();

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
        TaskCountResponse response = TaskCountResponse.builder()
            .userId(userId)
            .taskCount(0L)
            .build();

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
        TaskUpdateRequest request = TaskUpdateRequest.builder()
            .title("Updated Title")
            .description("Updated description")
            .status(TaskUpdateRequest.TaskStatus.IN_PROGRESS)
            .priority(TaskUpdateRequest.TaskPriority.URGENT)
            .build();

        TaskResponse response = TaskResponse.builder()
            .id(taskId)
            .title(request.getTitle())
            .description(request.getDescription())
            .status("IN_PROGRESS")
            .priority("URGENT")
            .updatedAt(LocalDateTime.now())
            .build();

        when(taskService.updateTask(eq(taskId), any(TaskUpdateRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}", taskId)
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
        TaskUpdateRequest request = TaskUpdateRequest.builder()
            .title("Updated Title")
            .build();

        when(taskService.updateTask(eq(taskId), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException(taskId));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));
    }

    @Test
    void updateTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        Long taskId = 1L;
        String longTitle = "a".repeat(256);
        String requestJson = "{"title":"" + longTitle + ""}";

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
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
        mockMvc.perform(delete("/v1/tasks/{taskId}", taskId))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long taskId = 999L;
        doThrow(new TaskNotFoundException(taskId)).when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/{taskId}", taskId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));
    }

    // ========== bulkCreateTasks Tests ==========

    @Test
    void bulkCreateTasks_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
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

        TaskResponse response1 = TaskResponse.builder()
            .id(1L)
            .title("Task 1")
            .build();

        TaskResponse response2 = TaskResponse.builder()
            .id(2L)
            .title("Task 2")
            .build();

        BulkTaskResponse bulkResponse = BulkTaskResponse.builder()
            .successCount(2)
            .failureCount(0)
            .tasks(Arrays.asList(response1, response2))
            .errors(Collections.emptyList())
            .build();

        when(taskService.bulkCreateTasks(anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.successCount").value(2))
            .andExpect(jsonPath("$.failureCount").value(0))
            .andExpect(jsonPath("$.tasks.length()").value(2))
            .andExpect(jsonPath("$.errors.length()").value(0));

        verify(taskService, times(1)).bulkCreateTasks(anyList());
    }

    @Test
    void bulkCreateTasks_PartialSuccess_ReturnsCreated() throws Exception {
        // Arrange
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

        TaskResponse response1 = TaskResponse.builder()
            .id(1L)
            .title("Task 1")
            .build();

        BulkTaskResponse bulkResponse = BulkTaskResponse.builder()
            .successCount(1)
            .failureCount(1)
            .tasks(Collections.singletonList(response1))
            .errors(Collections.singletonList("Failed to create task 'Task 2': Task limit exceeded"))
            .build();

        when(taskService.bulkCreateTasks(anyList())).thenReturn(bulkResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.successCount").value(1))
            .andExpect(jsonPath("$.failureCount").value(1))
            .andExpect(jsonPath("$.tasks.length()").value(1))
            .andExpect(jsonPath("$.errors.length()").value(1));
    }

    @Test
    void bulkCreateTasks_ExceedsLimit_ReturnsBadRequest() throws Exception {
        // Arrange
        List<TaskCreateRequest> requests = Collections.nCopies(101, TaskCreateRequest.builder().title("Task").userId(1L).priority(TaskCreateRequest.TaskPriority.LOW).build());

        when(taskService.bulkCreateTasks(anyList()))
            .thenThrow(new IllegalArgumentException("Cannot create more than 100 tasks at once"));

        // Act & Assert
        mockMvc.perform(post("/v1/tasks/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andExpect(status().isBadRequest());
    }
}