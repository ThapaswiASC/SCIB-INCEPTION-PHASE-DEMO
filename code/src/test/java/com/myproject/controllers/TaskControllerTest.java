package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.exceptions.GlobalExceptionHandler;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.exceptions.ValidationException;
import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskCreateRequest validCreateRequest;
    private TaskUpdateRequest validUpdateRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        validCreateRequest = new TaskCreateRequest(
            "Complete project documentation",
            "Write comprehensive documentation for the task management system",
            Priority.HIGH,
            LocalDateTime.now().plusDays(7)
        );

        validUpdateRequest = new TaskUpdateRequest(
            "Complete project documentation",
            "Write comprehensive documentation for the task management system",
            Priority.HIGH,
            TaskStatus.IN_PROGRESS,
            LocalDateTime.now().plusDays(7)
        );

        taskResponse = new TaskResponse(
            1L,
            "Complete project documentation",
            "Write comprehensive documentation for the task management system",
            Priority.HIGH,
            TaskStatus.PENDING,
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            LocalDateTime.now().plusDays(7),
            1L
        );
    }

    // ========== CREATE TASK TESTS ==========

    @Test
    void createTask_WithValidRequest_ReturnsCreated() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Complete project documentation"))
            .andExpect(jsonPath("$.description").value("Write comprehensive documentation for the task management system"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.createdAt").exists());

        verify(taskService, times(1)).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithBlankTitle_ReturnsBadRequest() throws Exception {
        TaskCreateRequest invalidRequest = new TaskCreateRequest("", "Valid description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").exists());

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithNullTitle_ReturnsBadRequest() throws Exception {
        TaskCreateRequest invalidRequest = new TaskCreateRequest(null, "Valid description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithTitleTooLong_ReturnsBadRequest() throws Exception {
        String longTitle = "a".repeat(201);
        TaskCreateRequest invalidRequest = new TaskCreateRequest(longTitle, "Valid description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithBlankDescription_ReturnsBadRequest() throws Exception {
        TaskCreateRequest invalidRequest = new TaskCreateRequest("Valid title", "", Priority.HIGH, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithDescriptionTooLong_ReturnsBadRequest() throws Exception {
        String longDescription = "a".repeat(1001);
        TaskCreateRequest invalidRequest = new TaskCreateRequest("Valid title", longDescription, Priority.HIGH, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).createTask(any(TaskCreateRequest.class));
    }

    @Test
    void createTask_WithValidationException_ReturnsBadRequest() throws Exception {
        List<String> errors = Arrays.asList("Title is required", "Description is required");
        when(taskService.createTask(any(TaskCreateRequest.class)))
            .thenThrow(new ValidationException("Validation failed", errors));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.details", hasSize(2)));
    }

    // ========== GET TASK TESTS ==========

    @Test
    void getTask_WithValidId_ReturnsOk() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Complete project documentation"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void getTask_WithNonExistentId_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(999L))
            .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    // ========== UPDATE TASK TESTS ==========

    @Test
    void updateTask_WithValidRequest_ReturnsOk() throws Exception {
        TaskResponse updatedResponse = new TaskResponse(
            1L,
            "Complete project documentation",
            "Write comprehensive documentation for the task management system",
            Priority.HIGH,
            TaskStatus.IN_PROGRESS,
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            LocalDateTime.of(2024, 1, 15, 14, 30, 0),
            LocalDateTime.now().plusDays(7),
            1L
        );

        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_WithNonExistentId_ReturnsNotFound() throws Exception {
        when(taskService.updateTask(eq(999L), any(TaskUpdateRequest.class)))
            .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void updateTask_WithBlankTitle_ReturnsBadRequest() throws Exception {
        TaskUpdateRequest invalidRequest = new TaskUpdateRequest("", "Valid description", Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).updateTask(any(Long.class), any(TaskUpdateRequest.class));
    }

    @Test
    void updateTask_WithNullStatus_ReturnsBadRequest() throws Exception {
        TaskUpdateRequest invalidRequest = new TaskUpdateRequest("Valid title", "Valid description", Priority.HIGH, null, LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(taskService, never()).updateTask(any(Long.class), any(TaskUpdateRequest.class));
    }

    // ========== DELETE TASK TESTS ==========

    @Test
    void deleteTask_WithValidId_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void deleteTask_WithNonExistentId_ReturnsNotFound() throws Exception {
        doThrow(new TaskNotFoundException(999L)).when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    // ========== GET ALL TASKS TESTS ==========

    @Test
    void getAllTasks_WithNoFilter_ReturnsAllTasks() throws Exception {
        TaskResponse task1 = new TaskResponse(1L, "Task 1", "Description 1", Priority.HIGH, TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);
        TaskResponse task2 = new TaskResponse(2L, "Task 2", "Description 2", Priority.MEDIUM, TaskStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(2), 1L);
        List<TaskResponse> tasks = Arrays.asList(task1, task2);

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));

        verify(taskService, times(1)).getAllTasks();
        verify(taskService, never()).getTasksByStatus(any(TaskStatus.class));
    }

    @Test
    void getAllTasks_WithStatusFilter_ReturnsFilteredTasks() throws Exception {
        TaskResponse task1 = new TaskResponse(1L, "Task 1", "Description 1", Priority.HIGH, TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);
        List<TaskResponse> tasks = Collections.singletonList(task1);

        when(taskService.getTasksByStatus(TaskStatus.PENDING)).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                .param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(taskService, times(1)).getTasksByStatus(TaskStatus.PENDING);
        verify(taskService, never()).getAllTasks();
    }

    @Test
    void getAllTasks_WithNoTasksFound_ReturnsEmptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
