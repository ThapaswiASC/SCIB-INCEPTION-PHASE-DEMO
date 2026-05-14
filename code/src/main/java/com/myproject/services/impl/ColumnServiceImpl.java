package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Column;
import com.myproject.services.interfaces.ColumnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnServiceImpl implements ColumnService {

    private static final Logger logger = LoggerFactory.getLogger(ColumnServiceImpl.class);

    @Autowired
    private ColumnDataStore columnDataStore;

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        logger.debug("Fetching column stats for: {}", columnId);
        
        Column column = columnDataStore.findById(columnId)
            .orElseThrow(() -> new ColumnNotFoundException(columnId));
        
        return new ColumnStatsResponse(
            column.getId(),
            column.getTaskCount(),
            column.getLastUpdated()
        );
    }

    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        logger.debug("Bulk updating column counts for {} columns", request.getUpdates().size());
        
        List<String> updatedColumns = new ArrayList<>();
        
        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            Column column = columnDataStore.findById(update.getColumnId())
                .orElseThrow(() -> new ColumnNotFoundException(update.getColumnId()));
            
            int newCount = column.getTaskCount() + update.getIncrement();
            if (newCount < 0) {
                newCount = 0;
            }
            
            column.setTaskCount(newCount);
            columnDataStore.save(column);
            updatedColumns.add(update.getColumnId());
        }
        
        logger.info("Bulk column count update completed for {} columns", updatedColumns.size());
        return new BulkUpdateColumnCountsResponse(true, updatedColumns);
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        logger.debug("Updating column counts: {} -> {}", oldColumnId, newColumnId);
        
        if (oldColumnId != null && columnDataStore.existsById(oldColumnId)) {
            columnDataStore.decrementTaskCount(oldColumnId);
        }
        
        if (newColumnId != null && columnDataStore.existsById(newColumnId)) {
            columnDataStore.incrementTaskCount(newColumnId);
        }
        
        logger.info("Column counts updated successfully");
    }
}