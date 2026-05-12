package com.myproject.models.datastores;

import com.myproject.models.entities.User;

import java.util.Optional;

public interface UserDataStore {
    User save(User user);
    Optional<User> findById(Long id);
    void incrementTaskCount(Long userId);
    void decrementTaskCount(Long userId);
}