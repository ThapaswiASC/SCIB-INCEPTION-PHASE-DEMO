package com.myproject.models.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BulkUpdateColumnCountsRequest {

    @NotNull
    @Valid
    private List<ColumnUpdate> updates;

    // Constructors
    public BulkUpdateColumnCountsRequest() {}

    public BulkUpdateColumnCountsRequest(List<ColumnUpdate> updates) {
        this.updates = updates;
    }

    // Getters and Setters
    public List<ColumnUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ColumnUpdate> updates) {
        this.updates = updates;
    }

    public static class ColumnUpdate {
        @NotNull
        private String columnId;

        @NotNull
        private Integer increment;

        // Constructors
        public ColumnUpdate() {}

        public ColumnUpdate(String columnId, Integer increment) {
            this.columnId = columnId;
            this.increment = increment;
        }

        // Getters and Setters
        public String getColumnId() {
            return columnId;
        }

        public void setColumnId(String columnId) {
            this.columnId = columnId;
        }

        public Integer getIncrement() {
            return increment;
        }

        public void setIncrement(Integer increment) {
            this.increment = increment;
        }
    }
}