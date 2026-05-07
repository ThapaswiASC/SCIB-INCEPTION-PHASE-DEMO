package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTaskResponse {
    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> createdTasks;
    private List<String> errors;
    private Integer totalCreated;
    private List<BulkTaskError> failures;
    
    public Integer getSuccessCount() {
        return this.successCount != null ? this.successCount : (this.totalCreated != null ? this.totalCreated : 0);
    }
    
    public Integer getFailureCount() {
        return this.failureCount != null ? this.failureCount : (this.failures != null ? this.failures.size() : 0);
    }
    
    public List<String> getErrors() {
        return this.errors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkTaskError {
        private Integer index;
        private String message;
        private Long taskId;
        private String error;
        
        public String getMessage() {
            return this.message != null ? this.message : this.error;
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureInfo {
        private Integer index;
        private String error;
        private String message;
        
        public String getError() {
            return this.error != null ? this.error : this.message;
        }
    }
}