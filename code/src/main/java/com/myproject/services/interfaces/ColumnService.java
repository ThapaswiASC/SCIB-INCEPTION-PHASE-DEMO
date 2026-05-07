package com.myproject.services.interfaces;

import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;

public interface ColumnService {

    ColumnStatsResponse getColumnStats(String columnId);

    BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request);

    void updateColumnCounts(String oldColumnId, String newColumnId);
}