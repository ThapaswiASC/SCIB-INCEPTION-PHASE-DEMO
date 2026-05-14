package com.myproject.models.entities;

import java.time.LocalDateTime;

public class Column {

    private String id;
    private String name;
    private String boardId;
    private Integer taskCount;
    private Integer position;
    private LocalDateTime lastUpdated;

    public Column() {
        this.taskCount = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    public Column(String id, String name, String boardId, Integer position) {
        this.id = id;
        this.name = name;
        this.boardId = boardId;
        this.position = position;
        this.taskCount = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
        this.lastUpdated = LocalDateTime.now();
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void incrementTaskCount() {
        this.taskCount++;
        this.lastUpdated = LocalDateTime.now();
    }

    public void decrementTaskCount() {
        if (this.taskCount > 0) {
            this.taskCount--;
            this.lastUpdated = LocalDateTime.now();
        }
    }
}