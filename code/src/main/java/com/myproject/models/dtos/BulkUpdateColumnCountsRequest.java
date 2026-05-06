package com.myproject.models.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BulkUpdateColumnCountsRequest {

    @NotNull(message = "Updates list is required")
    private List<ColumnUpdate> updates;

    public BulkUpdateColumnCountsRequest() {
    }

    public List<ColumnUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ColumnUpdate> updates) {
        this.updates = updates;
    }

    public static class ColumnUpdate {
        private String columnId;
        private Integer increment;

        public ColumnUpdate() {
        }

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