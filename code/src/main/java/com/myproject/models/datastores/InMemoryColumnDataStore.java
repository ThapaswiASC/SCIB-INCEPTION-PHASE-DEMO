package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryColumnDataStore implements ColumnDataStore {
    private final Map<String, Column> columns = new ConcurrentHashMap<>();

    public InMemoryColumnDataStore() {
        // Initialize default columns
        Column toDoColumn = new Column();
        toDoColumn.setId("to-do");
        toDoColumn.setName("To Do");
        toDoColumn.setTaskCount(0);
        toDoColumn.setPosition(0);
        columns.put("to-do", toDoColumn);

        Column inProgressColumn = new Column();
        inProgressColumn.setId("in-progress");
        inProgressColumn.setName("In Progress");
        inProgressColumn.setTaskCount(0);
        inProgressColumn.setPosition(1);
        columns.put("in-progress", inProgressColumn);

        Column doneColumn = new Column();
        doneColumn.setId("done");
        doneColumn.setName("Done");
        doneColumn.setTaskCount(0);
        doneColumn.setPosition(2);
        columns.put("done", doneColumn);
    }

    @Override
    public Column save(Column column) {
        column.setLastUpdated(LocalDateTime.now());
        columns.put(column.getId(), column);
        return column;
    }

    @Override
    public Optional<Column> findById(String id) {
        return Optional.ofNullable(columns.get(id));
    }

    @Override
    public void incrementTaskCount(String columnId, int increment) {
        Column column = columns.get(columnId);
        if (column != null) {
            column.setTaskCount(column.getTaskCount() + increment);
            column.setLastUpdated(LocalDateTime.now());
        }
    }
}