package com.myproject.utils;

import com.myproject.models.datastores.ColumnRepository;
import com.myproject.models.entities.BoardColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ColumnRepository columnRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default columns if they don't exist
        if (columnRepository.count() == 0) {
            createColumn("to-do", "To Do", "default-board", 0);
            createColumn("in-progress", "In Progress", "default-board", 1);
            createColumn("done", "Done", "default-board", 2);
        }
    }

    private void createColumn(String id, String name, String boardId, int position) {
        BoardColumn column = new BoardColumn();
        column.setId(id);
        column.setName(name);
        column.setBoardId(boardId);
        column.setPosition(position);
        column.setTaskCount(0);
        column.setLastUpdated(LocalDateTime.now());
        columnRepository.save(column);
    }
}
