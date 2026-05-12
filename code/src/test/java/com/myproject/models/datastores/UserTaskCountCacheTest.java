package com.myproject.models.datastores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTaskCountCacheTest {

    private UserTaskCountCache cache;

    @BeforeEach
    void setUp() {
        cache = new UserTaskCountCache();
    }

    @Test
    void getCount_NewUser_ReturnsZero() {
        // Act
        long count = cache.getCount(1L);

        // Assert
        assertEquals(0L, count);
    }

    @Test
    void incrementCount_NewUser_SetsCountToOne() {
        // Act
        cache.incrementCount(1L);

        // Assert
        assertEquals(1L, cache.getCount(1L));
    }

    @Test
    void incrementCount_ExistingUser_IncrementsCount() {
        // Arrange
        cache.incrementCount(1L);
        cache.incrementCount(1L);

        // Act
        cache.incrementCount(1L);

        // Assert
        assertEquals(3L, cache.getCount(1L));
    }

    @Test
    void incrementCount_MultipleTimes_AccumulatesCorrectly() {
        // Act
        for (int i = 0; i < 10; i++) {
            cache.incrementCount(1L);
        }

        // Assert
        assertEquals(10L, cache.getCount(1L));
    }

    @Test
    void decrementCount_ExistingUser_DecrementsCount() {
        // Arrange
        cache.incrementCount(1L);
        cache.incrementCount(1L);
        cache.incrementCount(1L);

        // Act
        cache.decrementCount(1L);

        // Assert
        assertEquals(2L, cache.getCount(1L));
    }

    @Test
    void decrementCount_CountAtZero_DoesNotDecrementBelowZero() {
        // Arrange
        cache.setCount(1L, 0L);

        // Act
        cache.decrementCount(1L);

        // Assert
        assertEquals(0L, cache.getCount(1L));
    }

    @Test
    void decrementCount_NonExistingUser_DoesNothing() {
        // Act
        cache.decrementCount(999L);

        // Assert
        assertEquals(0L, cache.getCount(999L));
    }

    @Test
    void setCount_NewUser_SetsCount() {
        // Act
        cache.setCount(1L, 100L);

        // Assert
        assertEquals(100L, cache.getCount(1L));
    }

    @Test
    void setCount_ExistingUser_OverwritesCount() {
        // Arrange
        cache.incrementCount(1L);
        cache.incrementCount(1L);

        // Act
        cache.setCount(1L, 50L);

        // Assert
        assertEquals(50L, cache.getCount(1L));
    }

    @Test
    void setCount_ZeroValue_SetsToZero() {
        // Arrange
        cache.incrementCount(1L);

        // Act
        cache.setCount(1L, 0L);

        // Assert
        assertEquals(0L, cache.getCount(1L));
    }

    @Test
    void clearCache_RemovesAllEntries() {
        // Arrange
        cache.incrementCount(1L);
        cache.incrementCount(2L);
        cache.incrementCount(3L);

        // Act
        cache.clearCache();

        // Assert
        assertEquals(0L, cache.getCount(1L));
        assertEquals(0L, cache.getCount(2L));
        assertEquals(0L, cache.getCount(3L));
    }

    @Test
    void multipleUsers_MaintainsSeparateCounts() {
        // Act
        cache.incrementCount(1L);
        cache.incrementCount(1L);
        cache.incrementCount(2L);
        cache.incrementCount(2L);
        cache.incrementCount(2L);
        cache.incrementCount(3L);

        // Assert
        assertEquals(2L, cache.getCount(1L));
        assertEquals(3L, cache.getCount(2L));
        assertEquals(1L, cache.getCount(3L));
    }

    @Test
    void concurrentOperations_HandlesCorrectly() throws InterruptedException {
        // Arrange
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                cache.incrementCount(1L);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                cache.incrementCount(1L);
            }
        });

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert
        assertEquals(200L, cache.getCount(1L));
    }

    @Test
    void fullCycle_IncrementDecrementSet_WorksCorrectly() {
        // Increment
        cache.incrementCount(1L);
        cache.incrementCount(1L);
        cache.incrementCount(1L);
        assertEquals(3L, cache.getCount(1L));

        // Decrement
        cache.decrementCount(1L);
        assertEquals(2L, cache.getCount(1L));

        // Set
        cache.setCount(1L, 10L);
        assertEquals(10L, cache.getCount(1L));

        // Clear
        cache.clearCache();
        assertEquals(0L, cache.getCount(1L));
    }

    @Test
    void largeCountValues_HandlesCorrectly() {
        // Act
        cache.setCount(1L, 10000L);
        cache.incrementCount(1L);

        // Assert
        assertEquals(10001L, cache.getCount(1L));
    }
}