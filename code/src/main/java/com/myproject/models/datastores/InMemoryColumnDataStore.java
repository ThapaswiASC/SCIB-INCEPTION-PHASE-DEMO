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
        BoardColumn toDoColumn = new BoardColumn();
        toDoColumn.setId("to-do");
        toDoColumn.setName("To Do");
        toDoColumn.setTaskCount(0);
        toDoColumn.setPosition(0);
        columns.put("to-do", toDoColumn);

        BoardColumn inProgressColumn = new BoardColumn();
        inProgressColumn.setId("in-progress");
        inProgressColumn.setName("In Progress");
        inProgressColumn.setTaskCount(0);
        inProgressColumn.setPosition(1);
        columns.put("in-progress", inProgressColumn);

        BoardColumn doneColumn = new BoardColumn();
        doneColumn.setId("done");
        doneColumn.setName("Done");
        doneColumn.setTaskCount(0);
        doneColumn.setPosition(2);
        columns.put("done", doneColumn);
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