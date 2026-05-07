package com.myproject.services.impl;

import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.ColumnRepository;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.Column;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        Column column = columnRepository.findById(columnId)
            .orElseThrow(() -> new TaskNotFoundException("Column not found with ID: " + columnId));

        return new ColumnStatsResponse(
            column.getId(),
            column.getTaskCount(),
            column.getLastUpdated()
        );
    }

    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        List<String> updatedColumns = new ArrayList<>();

        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            Column column = columnRepository.findById(update.getColumnId())
                .orElseGet(() -> createDefaultColumn(update.getColumnId()));

            column.setTaskCount(column.getTaskCount() + update.getIncrement());
            column.setLastUpdated(LocalDateTime.now());
            columnRepository.save(column);

            updatedColumns.add(update.getColumnId());
        }

        return new BulkUpdateColumnCountsResponse(true, updatedColumns);
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null && !oldColumnId.isEmpty()) {
            columnRepository.incrementTaskCount(oldColumnId, -1);
        }

        if (newColumnId != null && !newColumnId.isEmpty()) {
            Column column = columnRepository.findById(newColumnId)
                .orElseGet(() -> createDefaultColumn(newColumnId));
            column.setTaskCount(column.getTaskCount() + 1);
            columnRepository.save(column);
        }
    }

    private Column createDefaultColumn(String columnId) {
        Column column = new Column();
        column.setId(columnId);
        column.setName(columnId);
        column.setTaskCount(0);
        column.setLastUpdated(LocalDateTime.now());
        return columnRepository.save(column);
    }
}