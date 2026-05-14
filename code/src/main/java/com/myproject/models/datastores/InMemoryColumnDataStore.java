package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryColumnDataStore implements ColumnDataStore {
    private final Map<String, Column> columns = new ConcurrentHashMap<>();

    public InMemoryColumnDataStore() {
        // Initialize with default columns
        Column todoColumn = new Column("to-do", "To Do", "default-board", 0);
        Column inProgressColumn = new Column("in-progress", "In Progress", "default-board", 1);
        Column doneColumn = new Column("done", "Done", "default-board", 2);
        
        columns.put(todoColumn.getId(), todoColumn);
        columns.put(inProgressColumn.getId(), inProgressColumn);
        columns.put(doneColumn.getId(), doneColumn);
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
                .filter(column -> boardId.equals(column.getBoardId()))
                .sorted((c1, c2) -> c1.getPosition().compareTo(c2.getPosition()))
                .collect(Collectors.toList());
    }

    @Override
    public void incrementTaskCount(String columnId, int increment) {
        Column column = columns.get(columnId);
        if (column != null) {
            column.incrementTaskCount(increment);
        }
    }
}
