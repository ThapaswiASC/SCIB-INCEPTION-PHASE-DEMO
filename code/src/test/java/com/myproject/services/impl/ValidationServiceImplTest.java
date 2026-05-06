package com.myproject.services.impl;

import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.TaskCreateRequestValidated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ValidationServiceImpl validationService;

    // ========== validateTaskInput Tests ==========

    @Test
    void validateTaskInput_ValidRequest_ReturnsEmptyErrors() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("Valid description");
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateTaskInput_NullTitle_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setPriority("HIGH");
        // title is null

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Title")));
    }

    @Test
    void validateTaskInput_EmptyTitle_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("");
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Title")));
    }

    @Test
    void validateTaskInput_WhitespaceOnlyTitle_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("   ");
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Title")));
    }

    @Test
    void validateTaskInput_TitleTooLong_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(256)); // exceeds 255 character limit
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Title") && e.contains("255")));
    }

    @Test
    void validateTaskInput_DescriptionTooLong_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("a".repeat(10001)); // exceeds 10000 character limit
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Description") && e.contains("10000")));
    }

    @Test
    void validateTaskInput_NullPriority_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        // priority is null

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Priority")));
    }

    @Test
    void validateTaskInput_InvalidPriority_ReturnsError() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setPriority("INVALID_PRIORITY");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Priority")));
    }

    @Test
    void validateTaskInput_ValidPriorities_ReturnsEmptyErrors() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        String[] validPriorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL", "URGENT"};

        for (String priority : validPriorities) {
            TaskCreateRequestValidated request = new TaskCreateRequestValidated();
            request.setTitle("Valid Task");
            request.setPriority(priority);

            // Act
            List<String> errors = validationService.validateTaskInput(request);

            // Assert
            assertTrue(errors.isEmpty(), "Priority " + priority + " should be valid");
        }
    }

    @Test
    void validateTaskInput_SpecialCharactersInTitle_ReturnsEmptyErrors() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Task with special chars: @#$%^&*()");
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateTaskInput_MaxLengthTitle_ReturnsEmptyErrors() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("a".repeat(255)); // exactly 255 characters
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateTaskInput_MaxLengthDescription_ReturnsEmptyErrors() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTitleLength", 255);
        ReflectionTestUtils.setField(validationService, "maxDescriptionLength", 10000);

        TaskCreateRequestValidated request = new TaskCreateRequestValidated();
        request.setTitle("Valid Task");
        request.setDescription("a".repeat(10000)); // exactly 10000 characters
        request.setPriority("HIGH");

        // Act
        List<String> errors = validationService.validateTaskInput(request);

        // Assert
        assertTrue(errors.isEmpty());
    }

    // ========== validateTaskLimit Tests ==========

    @Test
    void validateTaskLimit_BelowLimit_DoesNotThrowException() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTasksPerUser", 10000);
        Long userId = 1L;
        when(taskRepository.countTasksByUserId(userId)).thenReturn(5000L);

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateTaskLimit(userId));
        verify(taskRepository).countTasksByUserId(userId);
    }

    @Test
    void validateTaskLimit_AtLimit_ThrowsException() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTasksPerUser", 10000);
        Long userId = 1L;
        when(taskRepository.countTasksByUserId(userId)).thenReturn(10000L);

        // Act & Assert
        assertThrows(TaskLimitExceededException.class, () -> {
            validationService.validateTaskLimit(userId);
        });
        verify(taskRepository).countTasksByUserId(userId);
    }

    @Test
    void validateTaskLimit_ExceedsLimit_ThrowsException() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTasksPerUser", 10000);
        Long userId = 1L;
        when(taskRepository.countTasksByUserId(userId)).thenReturn(15000L);

        // Act & Assert
        assertThrows(TaskLimitExceededException.class, () -> {
            validationService.validateTaskLimit(userId);
        });
        verify(taskRepository).countTasksByUserId(userId);
    }

    @Test
    void validateTaskLimit_ZeroTasks_DoesNotThrowException() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTasksPerUser", 10000);
        Long userId = 1L;
        when(taskRepository.countTasksByUserId(userId)).thenReturn(0L);

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateTaskLimit(userId));
        verify(taskRepository).countTasksByUserId(userId);
    }

    @Test
    void validateTaskLimit_OneBelowLimit_DoesNotThrowException() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "maxTasksPerUser", 10000);
        Long userId = 1L;
        when(taskRepository.countTasksByUserId(userId)).thenReturn(9999L);

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateTaskLimit(userId));
        verify(taskRepository).countTasksByUserId(userId);
    }

    // ========== validateStatusTransition Tests ==========

    @Test
    void validateStatusTransition_ToDoToInProgress_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("TO_DO", "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_PendingToInProgress_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("PENDING", "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_InProgressToDone_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("IN_PROGRESS", "DONE");
        });
    }

    @Test
    void validateStatusTransition_InProgressToCompleted_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("IN_PROGRESS", "COMPLETED");
        });
    }

    @Test
    void validateStatusTransition_InProgressToToDo_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("IN_PROGRESS", "TO_DO");
        });
    }

    @Test
    void validateStatusTransition_InProgressToPending_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("IN_PROGRESS", "PENDING");
        });
    }

    @Test
    void validateStatusTransition_DoneToInProgress_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("DONE", "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_CompletedToInProgress_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("COMPLETED", "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_ToDoToDone_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition("TO_DO", "DONE");
        });
    }

    @Test
    void validateStatusTransition_PendingToDone_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition("PENDING", "DONE");
        });
    }

    @Test
    void validateStatusTransition_CancelledToAny_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition("CANCELLED", "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_NullFromStatus_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition(null, "IN_PROGRESS");
        });
    }

    @Test
    void validateStatusTransition_NullToStatus_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition("TO_DO", null);
        });
    }

    @Test
    void validateStatusTransition_BothNull_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            validationService.validateStatusTransition(null, null);
        });
    }

    @Test
    void validateStatusTransition_CaseInsensitive_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("to_do", "in_progress");
        });
    }

    @Test
    void validateStatusTransition_MixedCase_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            validationService.validateStatusTransition("To_Do", "In_Progress");
        });
    }
}