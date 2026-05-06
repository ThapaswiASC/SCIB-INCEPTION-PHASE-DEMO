package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {
    
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }
    
    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }
    
    @Override
    public List<Task> findByUserId(Long userId) {
        return tasks.values().stream()
            .filter(task -> task.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Task> findByUserIdPaginated(Long userId, int page, int size) {
        return tasks.values().stream()
            .filter(task -> task.getUserId().equals(userId))
            .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }
    
    @Override
    public Long countByUserId(Long userId) {
        return tasks.values().stream()
            .filter(task -> task.getUserId().equals(userId))
            .count();
    }
    
    @Override
    public List<Task> findByColumnId(String columnId) {
        return tasks.values().stream()
            .filter(task -> columnId.equals(task.getColumnId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        tasks.remove(id);
    }
    
    @Override
    public List<Task> saveAll(List<Task> taskList) {
        return taskList.stream()
            .map(this::save)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
}