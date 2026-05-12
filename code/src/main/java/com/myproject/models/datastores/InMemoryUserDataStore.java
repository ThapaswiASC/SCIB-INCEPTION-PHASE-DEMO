package com.myproject.models.datastores;

import com.myproject.models.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserDataStore implements UserDataStore {
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void incrementTaskCount(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setTaskCount(user.getTaskCount() + 1);
            user.setUpdatedAt(LocalDateTime.now());
        }
    }

    @Override
    public void decrementTaskCount(Long userId) {
        User user = users.get(userId);
        if (user != null && user.getTaskCount() > 0) {
            user.setTaskCount(user.getTaskCount() - 1);
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
}