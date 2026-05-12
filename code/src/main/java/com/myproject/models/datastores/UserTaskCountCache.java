package com.myproject.models.datastores;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTaskCountCache {

    private final Map<Long, Long> countCache = new ConcurrentHashMap<>();

    public long getCount(Long userId) {
        return countCache.getOrDefault(userId, 0L);
    }

    public void incrementCount(Long userId) {
        countCache.merge(userId, 1L, Long::sum);
    }

    public void decrementCount(Long userId) {
        countCache.computeIfPresent(userId, (key, value) -> Math.max(0, value - 1));
    }

    public void setCount(Long userId, long count) {
        countCache.put(userId, count);
    }

    public void clearCache() {
        countCache.clear();
    }
}
