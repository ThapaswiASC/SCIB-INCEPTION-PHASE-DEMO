package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.Column;
import com.myproject.services.interfaces.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {
    
    private final ColumnDataStore columnDataStore;
    
    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        Column column = columnDataStore.findById(columnId)
            .orElseThrow(() -> new ColumnNotFoundException(columnId));
        
        return ColumnStatsResponse.builder()
            .columnId(column.getId())
            .taskCount(column.getTaskCount())
            .lastUpdated(column.getLastUpdated())
            .build();
    }
    
    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        List<String> updatedColumns = new ArrayList<>();
        
        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            Column column = columnDataStore.findById(update.getColumnId())
                .orElseThrow(() -> new ColumnNotFoundException(update.getColumnId()));
            
            columnDataStore.incrementTaskCount(update.getColumnId(), update.getIncrement());
            updatedColumns.add(update.getColumnId());
        }
        
        return BulkUpdateColumnCountsResponse.builder()
            .success(true)
            .updatedColumns(updatedColumns)
            .build();
    }
    
    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null && !oldColumnId.equals(newColumnId)) {
            columnDataStore.incrementTaskCount(oldColumnId, -1);
        }
        if (newColumnId != null) {
            columnDataStore.incrementTaskCount(newColumnId, 1);
        }
    }
}