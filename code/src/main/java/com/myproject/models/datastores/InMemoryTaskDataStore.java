package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {

    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
            task.setCreatedAt(LocalDateTime.now());
            task.setVersion(1L);
        } else {
            task.setUpdatedAt(LocalDateTime.now());
            if (task.getVersion() != null) {
                task.setVersion(task.getVersion() + 1);
            }
        }
        taskStore.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    @Override
    public List<Task> findByUserId(Long userId, int page, int size) {
        return taskStore.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserId(Long userId) {
        return taskStore.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        taskStore.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return taskStore.containsKey(id);
    }
}
