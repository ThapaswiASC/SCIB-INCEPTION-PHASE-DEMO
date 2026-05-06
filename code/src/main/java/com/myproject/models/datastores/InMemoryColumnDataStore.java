package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryColumnDataStore implements ColumnDataStore {
    
    private final Map<String, Column> columns = new ConcurrentHashMap<>();
    
    public InMemoryColumnDataStore() {
        // Initialize with default columns
        save(Column.builder()
            .id("to-do")
            .name("To Do")
            .boardId("default-board")
            .taskCount(0)
            .position(1)
            .lastUpdated(LocalDateTime.now())
            .build());
        
        save(Column.builder()
            .id("in-progress")
            .name("In Progress")
            .boardId("default-board")
            .taskCount(0)
            .position(2)
            .lastUpdated(LocalDateTime.now())
            .build());
        
        save(Column.builder()
            .id("done")
            .name("Done")
            .boardId("default-board")
            .taskCount(0)
            .position(3)
            .lastUpdated(LocalDateTime.now())
            .build());
    }
    
    @Override
    public Column save(Column column) {
        columns.put(column.getId(), column);
        return column;
    }
    
    @Override
    public Optional<Column> findById(String id) {
        return Optional.ofNullable(columns.get(id));
    }
    
    @Override
    public List<Column> findByBoardId(String boardId) {
        return columns.values().stream()
            .filter(column -> column.getBoardId().equals(boardId))
            .sorted(Comparator.comparing(Column::getPosition))
            .collect(Collectors.toList());
    }
    
    @Override
    public void incrementTaskCount(String columnId, int increment) {
        Column column = columns.get(columnId);
        if (column != null) {
            column.setTaskCount(column.getTaskCount() + increment);
            column.setLastUpdated(LocalDateTime.now());
        }
    }
    
    @Override
    public List<Column> findAll() {
        return new ArrayList<>(columns.values());
    }
}