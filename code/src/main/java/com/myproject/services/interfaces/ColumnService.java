package com.myproject.services.interfaces;

import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.dtos.ColumnTaskCountResponse;

public interface ColumnService {

    ColumnTaskCountResponse getColumnTaskCount(String columnId);
    
    ColumnStatsResponse getColumnStats(String columnId);
    
    BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request);
    
    void updateColumnCounts(String oldColumnId, String newColumnId);
}