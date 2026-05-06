package com.myproject.models.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "columns")
public class Column {

    @Id
    @jakarta.persistence.Column(length = 100)
    private String id;

    @jakarta.persistence.Column(nullable = false, length = 255)
    private String name;

    @jakarta.persistence.Column(name = "board_id", length = 100)
    private String boardId;

    @jakarta.persistence.Column(name = "task_count")
    private Integer taskCount = 0;

    @jakarta.persistence.Column
    private Integer position;

    @jakarta.persistence.Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Column() {
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
}