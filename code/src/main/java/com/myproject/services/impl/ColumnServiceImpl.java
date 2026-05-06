package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnRepository;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.BoardColumn;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;

    public ColumnServiceImpl(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        BoardColumn column = columnRepository.findById(columnId)
            .orElseThrow(() -> new ColumnNotFoundException(columnId));

        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(column.getId());
        response.setTaskCount(column.getTaskCount());
        response.setLastUpdated(LocalDateTime.now());

        return response;
    }

    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        List<String> updatedColumns = new ArrayList<>();

        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            String columnId = update.getColumnId();
            Integer increment = update.getIncrement();

            if (columnRepository.existsById(columnId)) {
                columnRepository.incrementTaskCount(columnId, increment);
                updatedColumns.add(columnId);
            }
        }

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(updatedColumns);

        return response;
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null && columnRepository.existsById(oldColumnId)) {
            columnRepository.incrementTaskCount(oldColumnId, -1);
        }

        if (newColumnId != null && columnRepository.existsById(newColumnId)) {
            columnRepository.incrementTaskCount(newColumnId, 1);
        }
    }
}