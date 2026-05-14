package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class TaskDataStoreImpl implements TaskDataStore {

    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        taskStore.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskStore.values());
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return taskStore.values().stream()
            .filter(task -> task.getUserId().equals(userId))
            .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByUserIdPaginated(Long userId, int page, int size) {
        return taskStore.values().stream()
            .filter(task -> task.getUserId().equals(userId))
            .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public Long countByUserId(Long userId) {
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

    @Override
    public List<Task> saveAll(List<Task> tasks) {
        List<Task> savedTasks = new ArrayList<>();
        for (Task task : tasks) {
            savedTasks.add(save(task));
        }
        return savedTasks;
    }

    @Override
    public List<Task> findByColumnId(String columnId) {
        return taskStore.values().stream()
            .filter(task -> columnId.equals(task.getColumnId()))
            .collect(Collectors.toList());
    }

    @Override
    public Long countByColumnId(String columnId) {
        return taskStore.values().stream()
            .filter(task -> columnId.equals(task.getColumnId()))
            .count();
    }
}