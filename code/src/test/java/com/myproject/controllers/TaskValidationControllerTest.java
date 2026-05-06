package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.models.dtos.TaskCreateRequestValidated;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.ValidationResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskValidationController.class)
class TaskValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    // ========== createTaskWithValidation Tests ==========

    @Test
    void createTaskWithValidation_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("Task description");
        request.setPriority("HIGH");

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setStatus("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTaskValidated(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Valid Task"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).createTaskValidated(any(TaskCreateRequestValidated.class));
    }

    @Test
    void createTaskWithValidation_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setPriority("HIGH");
        // title is null

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_EmptyTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("");
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_WhitespaceOnlyTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("   ");
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_TitleTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(256)); // exceeds 255 character limit
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_DescriptionTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("a".repeat(10001)); // exceeds 10000 character limit
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_MissingPriority_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        // priority is null

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void createTaskWithValidation_SpecialCharactersInTitle_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Task with special chars: @#$%^&*()");
        request.setDescription("Description with special chars: <>&\"");
        request.setPriority("HIGH");

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setStatus("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTaskValidated(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Task with special chars: @#$%^&*()"));
    }

    @Test
    void createTaskWithValidation_MaxLengthTitle_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(255)); // exactly 255 characters
        request.setPriority("HIGH");

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setPriority(request.getPriority());
        response.setStatus("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTaskValidated(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void createTaskWithValidation_MaxLengthDescription_ReturnsCreated() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("a".repeat(10000)); // exactly 10000 characters
        request.setPriority("HIGH");

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setStatus("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTaskValidated(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    // ========== validateTaskInput Tests ==========

    @Test
    void validateTaskInput_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("Task description");
        request.setPriority("HIGH");

        ValidationResponse response = new ValidationResponse();
        response.setValid(true);
        response.setErrors(Collections.emptyList());

        when(taskService.validateTaskInput(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors.length()").value(0));

        verify(taskService, times(1)).validateTaskInput(any(TaskCreateRequestValidated.class));
    }

    @Test
    void validateTaskInput_InvalidRequest_ReturnsOkWithErrors() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(256)); // exceeds limit
        request.setPriority("INVALID_PRIORITY");

        ValidationResponse response = new ValidationResponse();
        response.setValid(false);
        response.setErrors(Arrays.asList(
            "Title cannot exceed 255 characters",
            "Priority must be one of: LOW, MEDIUM, HIGH, CRITICAL, URGENT"
        ));

        when(taskService.validateTaskInput(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void validateTaskInput_MissingTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setPriority("HIGH");
        // title is null

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void validateTaskInput_EmptyTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("");
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void validateTaskInput_WhitespaceOnlyTitle_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("   ");
        request.setPriority("HIGH");

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void validateTaskInput_SpecialCharacters_ReturnsOk() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Task with special chars: @#$%^&*()");
        request.setDescription("Description with special chars: <>&\"");
        request.setPriority("HIGH");

        ValidationResponse response = new ValidationResponse();
        response.setValid(true);
        response.setErrors(Collections.emptyList());

        when(taskService.validateTaskInput(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void validateTaskInput_MaxLengthTitle_ReturnsOk() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(255)); // exactly 255 characters
        request.setPriority("HIGH");

        ValidationResponse response = new ValidationResponse();
        response.setValid(true);
        response.setErrors(Collections.emptyList());

        when(taskService.validateTaskInput(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void validateTaskInput_MaxLengthDescription_ReturnsOk() throws Exception {
        // Arrange
        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("a".repeat(10000)); // exactly 10000 characters
        request.setPriority("HIGH");

        ValidationResponse response = new ValidationResponse();
        response.setValid(true);
        response.setErrors(Collections.emptyList());

        when(taskService.validateTaskInput(any(TaskCreateRequestValidated.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/tasks/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }
}