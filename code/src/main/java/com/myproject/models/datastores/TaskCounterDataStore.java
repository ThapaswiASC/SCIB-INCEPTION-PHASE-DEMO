package com.myproject.models.datastores;

import com.myproject.models.entities.TaskCounter;

import java.util.Optional;

public interface TaskCounterDataStore {
    TaskCounter save(TaskCounter counter);
    Optional<TaskCounter> findByUserId(String userId);
    void incrementTaskCount(String userId);
    void decrementTaskCount(String userId);
}