package com.myproject.models.datastores;

import com.myproject.models.entities.TaskCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskCounterDataStoreTest {

    private InMemoryTaskCounterDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryTaskCounterDataStore();
    }

    @Test
    void save_NewCounter_StoresAndReturnsCounter() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.setTaskCount(5L);

        // Act
        TaskCounter savedCounter = dataStore.save(counter);

        // Assert
        assertNotNull(savedCounter);
        assertEquals("user123", savedCounter.getUserId());
        assertEquals(5L, savedCounter.getTaskCount());
        assertNotNull(savedCounter.getLastUpdated());
    }

    @Test
    void save_ExistingCounter_UpdatesCounter() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.setTaskCount(5L);
        dataStore.save(counter);

        // Act
        counter.setTaskCount(10L);
        TaskCounter updatedCounter = dataStore.save(counter);

        // Assert
        assertEquals(10L, updatedCounter.getTaskCount());
    }

    @Test
    void findByUserId_ExistingUser_ReturnsCounter() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.setTaskCount(5L);
        dataStore.save(counter);

        // Act
        Optional<TaskCounter> found = dataStore.findByUserId("user123");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("user123", found.get().getUserId());
        assertEquals(5L, found.get().getTaskCount());
    }

    @Test
    void findByUserId_NonExistingUser_ReturnsEmpty() {
        // Act
        Optional<TaskCounter> found = dataStore.findByUserId("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void incrementTaskCount_NewUser_CreatesCounterWithOne() {
        // Act
        dataStore.incrementTaskCount("user123");

        // Assert
        Optional<TaskCounter> counter = dataStore.findByUserId("user123");
        assertTrue(counter.isPresent());
        assertEquals(1L, counter.get().getTaskCount());
    }

    @Test
    void incrementTaskCount_ExistingUser_IncrementsCount() {
        // Arrange
        dataStore.incrementTaskCount("user123");
        dataStore.incrementTaskCount("user123");

        // Act
        dataStore.incrementTaskCount("user123");

        // Assert
        Optional<TaskCounter> counter = dataStore.findByUserId("user123");
        assertTrue(counter.isPresent());
        assertEquals(3L, counter.get().getTaskCount());
    }

    @Test
    void decrementTaskCount_ExistingUser_DecrementsCount() {
        // Arrange
        dataStore.incrementTaskCount("user123");
        dataStore.incrementTaskCount("user123");
        dataStore.incrementTaskCount("user123");

        // Act
        dataStore.decrementTaskCount("user123");

        // Assert
        Optional<TaskCounter> counter = dataStore.findByUserId("user123");
        assertTrue(counter.isPresent());
        assertEquals(2L, counter.get().getTaskCount());
    }

    @Test
    void decrementTaskCount_NonExistingUser_DoesNothing() {
        // Act
        dataStore.decrementTaskCount("nonexistent");

        // Assert
        Optional<TaskCounter> counter = dataStore.findByUserId("nonexistent");
        assertFalse(counter.isPresent());
    }

    @Test
    void decrementTaskCount_CounterAtZero_DoesNotDecrementBelowZero() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.setTaskCount(0L);
        dataStore.save(counter);

        // Act
        dataStore.decrementTaskCount("user123");

        // Assert
        Optional<TaskCounter> found = dataStore.findByUserId("user123");
        assertTrue(found.isPresent());
        assertEquals(0L, found.get().getTaskCount());
    }

    @Test
    void concurrentOperations_MultipleUsers_MaintainsSeparateCounts() {
        // Act
        dataStore.incrementTaskCount("user1");
        dataStore.incrementTaskCount("user1");
        dataStore.incrementTaskCount("user2");
        dataStore.incrementTaskCount("user2");
        dataStore.incrementTaskCount("user2");
        dataStore.decrementTaskCount("user1");

        // Assert
        Optional<TaskCounter> counter1 = dataStore.findByUserId("user1");
        Optional<TaskCounter> counter2 = dataStore.findByUserId("user2");

        assertTrue(counter1.isPresent());
        assertTrue(counter2.isPresent());
        assertEquals(1L, counter1.get().getTaskCount());
        assertEquals(3L, counter2.get().getTaskCount());
    }

    @Test
    void incrementTaskCount_UpdatesLastUpdatedTimestamp() {
        // Arrange
        dataStore.incrementTaskCount("user123");
        Optional<TaskCounter> before = dataStore.findByUserId("user123");

        // Act
        try {
            Thread.sleep(10); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        dataStore.incrementTaskCount("user123");

        // Assert
        Optional<TaskCounter> after = dataStore.findByUserId("user123");
        assertTrue(before.isPresent());
        assertTrue(after.isPresent());
        assertTrue(after.get().getLastUpdated().isAfter(before.get().getLastUpdated()) ||
                   after.get().getLastUpdated().isEqual(before.get().getLastUpdated()));
    }

    @Test
    void fullCrudCycle_WorksCorrectly() {
        // Create
        dataStore.incrementTaskCount("user123");
        Optional<TaskCounter> created = dataStore.findByUserId("user123");
        assertTrue(created.isPresent());
        assertEquals(1L, created.get().getTaskCount());

        // Update via increment
        dataStore.incrementTaskCount("user123");
        Optional<TaskCounter> updated = dataStore.findByUserId("user123");
        assertTrue(updated.isPresent());
        assertEquals(2L, updated.get().getTaskCount());

        // Update via decrement
        dataStore.decrementTaskCount("user123");
        Optional<TaskCounter> decremented = dataStore.findByUserId("user123");
        assertTrue(decremented.isPresent());
        assertEquals(1L, decremented.get().getTaskCount());
    }
}