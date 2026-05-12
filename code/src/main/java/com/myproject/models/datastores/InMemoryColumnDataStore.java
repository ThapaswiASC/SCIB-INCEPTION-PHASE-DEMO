package com.myproject.models.datastores;

import com.myproject.models.entities.BoardColumn;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryColumnDataStore implements ColumnDataStore {
    private final Map<String, BoardColumn> columns = new ConcurrentHashMap<>();

    public InMemoryColumnDataStore() {
        // Initialize default columns
        initializeDefaultColumns();
    }

    private void initializeDefaultColumns() {
        createDefaultColumn("to-do", "To Do", "default-board", 0);
        createDefaultColumn("in-progress", "In Progress", "default-board", 1);
        createDefaultColumn("done", "Done", "default-board", 2);
    }

    private void createDefaultColumn(String id, String name, String boardId, int position) {
        BoardColumn column = new BoardColumn();
        column.setId(id);
        column.setName(name);
        column.setBoardId(boardId);
        column.setPosition(position);
        column.setTaskCount(0);
        column.setLastUpdated(LocalDateTime.now());
        columns.put(id, column);
    }

    @Override
    public BoardColumn save(BoardColumn column) {
        column.setLastUpdated(LocalDateTime.now());
        columns.put(column.getId(), column);
        return column;
    }

    @Override
    public Optional<BoardColumn> findById(String id) {
        return Optional.ofNullable(columns.get(id));
    }

    @Override
    public void incrementTaskCount(String columnId, int increment) {
        BoardColumn column = columns.get(columnId);
        if (column != null) {
            column.setTaskCount(column.getTaskCount() + increment);
            column.setLastUpdated(LocalDateTime.now());
        }
    }
}
