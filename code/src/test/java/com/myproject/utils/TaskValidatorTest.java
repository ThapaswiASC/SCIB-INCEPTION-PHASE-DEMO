package com.myproject.utils;

import com.myproject.exceptions.ValidationException;
import com.myproject.models.dtos.Priority;
import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.dtos.TaskUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskValidatorTest {

    private TaskValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TaskValidator();
    }

    @Test
    void validateCreateRequest_WithValidRequest_DoesNotThrowException() {
        TaskCreateRequest request = new TaskCreateRequest(
            "Valid Title",
            "Valid Description",
            Priority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequest_WithNullRequest_ThrowsValidationException() {
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void validateCreateRequest_WithNullTitle_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest(null, "Valid Description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertEquals("Validation failed", exception.getMessage());
        assertTrue(exception.getErrors().contains("Title is required"));
    }

    @Test
    void validateCreateRequest_WithEmptyTitle_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest("", "Valid Description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title is required"));
    }

    @Test
    void validateCreateRequest_WithBlankTitle_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest("   ", "Valid Description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title is required"));
    }

    @Test
    void validateCreateRequest_WithTitleTooLong_ThrowsValidationException() {
        String longTitle = "a".repeat(201);
        TaskCreateRequest request = new TaskCreateRequest(longTitle, "Valid Description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title must not exceed 200 characters"));
    }

    @Test
    void validateCreateRequest_WithTitleExactly200Characters_DoesNotThrowException() {
        String title = "a".repeat(200);
        TaskCreateRequest request = new TaskCreateRequest(title, "Valid Description", Priority.HIGH, LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequest_WithNullDescription_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest("Valid Title", null, Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateCreateRequest_WithEmptyDescription_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest("Valid Title", "", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateCreateRequest_WithBlankDescription_ThrowsValidationException() {
        TaskCreateRequest request = new TaskCreateRequest("Valid Title", "   ", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateCreateRequest_WithDescriptionTooLong_ThrowsValidationException() {
        String longDescription = "a".repeat(1001);
        TaskCreateRequest request = new TaskCreateRequest("Valid Title", longDescription, Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description must not exceed 1000 characters"));
    }

    @Test
    void validateCreateRequest_WithDescriptionExactly1000Characters_DoesNotThrowException() {
        String description = "a".repeat(1000);
        TaskCreateRequest request = new TaskCreateRequest("Valid Title", description, Priority.HIGH, LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequest_WithMultipleErrors_ThrowsValidationExceptionWithAllErrors() {
        TaskCreateRequest request = new TaskCreateRequest("", "", Priority.HIGH, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateCreateRequest(request)
        );

        assertEquals(2, exception.getErrors().size());
        assertTrue(exception.getErrors().contains("Title is required"));
        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateUpdateRequest_WithValidRequest_DoesNotThrowException() {
        TaskUpdateRequest request = new TaskUpdateRequest(
            "Valid Title",
            "Valid Description",
            Priority.HIGH,
            TaskStatus.IN_PROGRESS,
            LocalDateTime.now().plusDays(1)
        );

        assertDoesNotThrow(() -> validator.validateUpdateRequest(request));
    }

    @Test
    void validateUpdateRequest_WithNullRequest_ThrowsValidationException() {
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void validateUpdateRequest_WithNullTitle_ThrowsValidationException() {
        TaskUpdateRequest request = new TaskUpdateRequest(null, "Valid Description", Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title is required"));
    }

    @Test
    void validateUpdateRequest_WithEmptyTitle_ThrowsValidationException() {
        TaskUpdateRequest request = new TaskUpdateRequest("", "Valid Description", Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title is required"));
    }

    @Test
    void validateUpdateRequest_WithTitleTooLong_ThrowsValidationException() {
        String longTitle = "a".repeat(201);
        TaskUpdateRequest request = new TaskUpdateRequest(longTitle, "Valid Description", Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Title must not exceed 200 characters"));
    }

    @Test
    void validateUpdateRequest_WithNullDescription_ThrowsValidationException() {
        TaskUpdateRequest request = new TaskUpdateRequest("Valid Title", null, Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateUpdateRequest_WithEmptyDescription_ThrowsValidationException() {
        TaskUpdateRequest request = new TaskUpdateRequest("Valid Title", "", Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description is required"));
    }

    @Test
    void validateUpdateRequest_WithDescriptionTooLong_ThrowsValidationException() {
        String longDescription = "a".repeat(1001);
        TaskUpdateRequest request = new TaskUpdateRequest("Valid Title", longDescription, Priority.HIGH, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Description must not exceed 1000 characters"));
    }

    @Test
    void validateUpdateRequest_WithNullStatus_ThrowsValidationException() {
        TaskUpdateRequest request = new TaskUpdateRequest("Valid Title", "Valid Description", Priority.HIGH, null, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertTrue(exception.getErrors().contains("Status is required"));
    }

    @Test
    void validateUpdateRequest_WithAllValidStatuses_DoesNotThrowException() {
        for (TaskStatus status : TaskStatus.values()) {
            TaskUpdateRequest request = new TaskUpdateRequest(
                "Valid Title",
                "Valid Description",
                Priority.HIGH,
                status,
                LocalDateTime.now().plusDays(1)
            );

            assertDoesNotThrow(() -> validator.validateUpdateRequest(request));
        }
    }

    @Test
    void validateUpdateRequest_WithMultipleErrors_ThrowsValidationExceptionWithAllErrors() {
        TaskUpdateRequest request = new TaskUpdateRequest("", "", Priority.HIGH, null, LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> validator.validateUpdateRequest(request)
        );

        assertEquals(3, exception.getErrors().size());
        assertTrue(exception.getErrors().contains("Title is required"));
        assertTrue(exception.getErrors().contains("Description is required"));
        assertTrue(exception.getErrors().contains("Status is required"));
    }
}
