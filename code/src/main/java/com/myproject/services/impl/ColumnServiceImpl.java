package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.ColumnTaskCountResponse;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private TaskDataStore taskDataStore;

    @Override
    public ColumnTaskCountResponse getColumnTaskCount(String columnId) {
        Long count = taskDataStore.countByColumnId(columnId);
        
        if (count == null) {
            throw new ColumnNotFoundException(columnId);
        }

        return ColumnTaskCountResponse.builder()
                .columnId(columnId)
                .taskCount(count.intValue())
                .build();
    }
}