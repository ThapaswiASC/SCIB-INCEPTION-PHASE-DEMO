package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;

public interface ColumnService {

    ColumnStatsResponse getColumnStats(String columnId);

    BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request);

    void updateColumnCounts(String oldColumnId, String newColumnId);
}