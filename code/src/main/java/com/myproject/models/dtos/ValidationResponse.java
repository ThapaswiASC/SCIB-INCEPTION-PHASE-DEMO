package com.myproject.models.dtos;

import java.util.List;

public class ValidationResponse {

    private Boolean valid;
    private List<String> errors;

    public ValidationResponse() {
    }

    public ValidationResponse(Boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}