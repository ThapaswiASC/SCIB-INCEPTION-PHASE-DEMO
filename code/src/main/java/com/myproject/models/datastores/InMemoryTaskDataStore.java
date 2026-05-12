package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {

    private final Map<UUID, Task> taskStore = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID());
        }
        task.setUpdatedAt(LocalDateTime.now());
        task.setVersion(task.getVersion() == null ? 0L : task.getVersion() + 1);
        taskStore.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    @Override
    public List<Task> findByUserId(String userId, int page, int size, String sort) {
        List<Task> userTasks = taskStore.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .sorted((t1, t2) -> {
                    if (sort.contains("desc")) {
                        return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                    }
                    return t1.getCreatedAt().compareTo(t2.getCreatedAt());
                })
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, userTasks.size());
        
        if (start >= userTasks.size()) {
            return Collections.emptyList();
        }
        
        return userTasks.subList(start, end);
    }

    @Override
    public long countByUserId(String userId) {
        return taskStore.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .count();
    }

    @Override
    public void deleteById(UUID id) {
        taskStore.remove(id);
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, String userId) {
        Task task = taskStore.get(id);
        return task != null && task.getUserId().equals(userId);
    }
}