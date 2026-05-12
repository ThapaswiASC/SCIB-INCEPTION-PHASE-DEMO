package com.myproject.controllers;

import com.myproject.models.dtos.TaskValidationRequest;
import com.myproject.models.dtos.ValidationErrorResponse;
import com.myproject.models.dtos.ValidationResponse;
import com.myproject.services.interfaces.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class ValidationController {
    private final ValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateInput(
            @Valid @RequestBody TaskValidationRequest request,
            BindingResult bindingResult) {
        log.info("Validating task input");
        
        if (bindingResult.hasErrors()) {
            List<ValidationErrorResponse.ValidationError> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> ValidationErrorResponse.ValidationError.builder()
                            .field(error.getField())
                            .message(error.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
            
            ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                    .valid(false)
                    .errors(errors)
                    .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        ValidationResponse response = validationService.validateInput(request);
        
        if (response.getValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}