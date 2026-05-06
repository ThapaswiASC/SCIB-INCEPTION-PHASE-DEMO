package com.myproject.models.datastores;

import com.myproject.models.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserDataStore implements UserDataStore {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public InMemoryUserDataStore() {
        // Initialize with a default user
        save(User.builder()
            .id(1L)
            .username("testuser")
            .email("testuser@example.com")
            .taskCount(0L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }
    
    @Override
    public void incrementTaskCount(Long userId, long increment) {
        User user = users.get(userId);
        if (user != null) {
            user.setTaskCount(user.getTaskCount() + increment);
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
}