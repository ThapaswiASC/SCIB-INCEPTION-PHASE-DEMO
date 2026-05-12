package com.myproject.services.interfaces;

import com.myproject.models.dtos.BulkColumnUpdateRequest;
import com.myproject.models.dtos.BulkColumnUpdateResponse;
import com.myproject.models.dtos.ColumnStatsResponse;

public interface ColumnService {
    ColumnStatsResponse getColumnStats(String columnId);
    BulkColumnUpdateResponse bulkUpdateColumnCounts(BulkColumnUpdateRequest request);
    void updateColumnCounts(String oldColumnId, String newColumnId);
}