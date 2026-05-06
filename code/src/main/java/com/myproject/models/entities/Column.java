package com.myproject.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Column {
    private String id;
    private String name;
    private String boardId;
    private Integer taskCount;
    private Integer position;
    private LocalDateTime lastUpdated;
}