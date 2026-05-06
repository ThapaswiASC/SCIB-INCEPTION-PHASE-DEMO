package com.myproject.models.datastores;

import com.myproject.models.entities.BoardColumn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryColumnDataStoreTest {

    private InMemoryColumnDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryColumnDataStore();
    }

    // ========== Initialization Tests ==========

    @Test
    void constructor_InitializesDefaultColumns() {
        // Act
        Optional<BoardColumn> toDoColumn = dataStore.findById("to-do");
        Optional<BoardColumn> inProgressColumn = dataStore.findById("in-progress");
        Optional<BoardColumn> doneColumn = dataStore.findById("done");

        // Assert
        assertTrue(toDoColumn.isPresent());
        assertEquals("To Do", toDoColumn.get().getName());
        assertEquals(0, toDoColumn.get().getTaskCount());
        assertEquals(0, toDoColumn.get().getPosition());

        assertTrue(inProgressColumn.isPresent());
        assertEquals("In Progress", inProgressColumn.get().getName());
        assertEquals(0, inProgressColumn.get().getTaskCount());
        assertEquals(1, inProgressColumn.get().getPosition());

        assertTrue(doneColumn.isPresent());
        assertEquals("Done", doneColumn.get().getName());
        assertEquals(0, doneColumn.get().getTaskCount());
        assertEquals(2, doneColumn.get().getPosition());
    }

    // ========== save Tests ==========

    @Test
    void save_NewColumn_SavesColumn() {
        // Arrange
        BoardColumn column = new BoardColumn();
        column.setId("custom-column");
        column.setName("Custom Column");
        column.setTaskCount(5);
        column.setPosition(3);

        // Act
        BoardColumn savedColumn = dataStore.save(column);

        // Assert
        assertNotNull(savedColumn);
        assertEquals("custom-column", savedColumn.getId());
        assertEquals("Custom Column", savedColumn.getName());
        assertEquals(5, savedColumn.getTaskCount());
        assertNotNull(savedColumn.getLastUpdated());
    }

    @Test
    void save_ExistingColumn_UpdatesColumn() {
        // Arrange
        Optional<BoardColumn> toDoColumn = dataStore.findById("to-do");
        assertTrue(toDoColumn.isPresent());
        BoardColumn column = toDoColumn.get();
        column.setTaskCount(10);

        // Act
        BoardColumn savedColumn = dataStore.save(column);

        // Assert
        assertEquals(10, savedColumn.getTaskCount());
        assertNotNull(savedColumn.getLastUpdated());
    }

    @Test
    void save_UpdatesLastUpdatedTimestamp() {
        // Arrange
        BoardColumn column = new BoardColumn();
        column.setId("test-column");
        column.setName("Test Column");
        LocalDateTime beforeSave = LocalDateTime.now();

        // Act
        BoardColumn savedColumn = dataStore.save(column);

        // Assert
        assertNotNull(savedColumn.getLastUpdated());
        assertTrue(savedColumn.getLastUpdated().isAfter(beforeSave) ||
                   savedColumn.getLastUpdated().isEqual(beforeSave));
    }

    // ========== findById Tests ==========

    @Test
    void findById_ExistingColumn_ReturnsColumn() {
        // Act
        Optional<BoardColumn> column = dataStore.findById("to-do");

        // Assert
        assertTrue(column.isPresent());
        assertEquals("to-do", column.get().getId());
        assertEquals("To Do", column.get().getName());
    }

    @Test
    void findById_NonExistingColumn_ReturnsEmpty() {
        // Act
        Optional<BoardColumn> column = dataStore.findById("non-existing");

        // Assert
        assertFalse(column.isPresent());
    }

    @Test
    void findById_InProgressColumn_ReturnsColumn() {
        // Act
        Optional<BoardColumn> column = dataStore.findById("in-progress");

        // Assert
        assertTrue(column.isPresent());
        assertEquals("in-progress", column.get().getId());
        assertEquals("In Progress", column.get().getName());
    }

    @Test
    void findById_DoneColumn_ReturnsColumn() {
        // Act
        Optional<BoardColumn> column = dataStore.findById("done");

        // Assert
        assertTrue(column.isPresent());
        assertEquals("done", column.get().getId());
        assertEquals("Done", column.get().getName());
    }

    // ========== incrementTaskCount Tests ==========

    @Test
    void incrementTaskCount_ExistingColumn_IncrementsCount() {
        // Arrange
        Optional<BoardColumn> column = dataStore.findById("to-do");
        assertTrue(column.isPresent());
        int initialCount = column.get().getTaskCount();

        // Act
        dataStore.incrementTaskCount("to-do", 5);

        // Assert
        Optional<BoardColumn> updatedColumn = dataStore.findById("to-do");
        assertTrue(updatedColumn.isPresent());
        assertEquals(initialCount + 5, updatedColumn.get().getTaskCount());
    }

    @Test
    void incrementTaskCount_NegativeIncrement_DecrementsCount() {
        // Arrange
        dataStore.incrementTaskCount("to-do", 10);

        // Act
        dataStore.incrementTaskCount("to-do", -3);

        // Assert
        Optional<BoardColumn> column = dataStore.findById("to-do");
        assertTrue(column.isPresent());
        assertEquals(7, column.get().getTaskCount());
    }

    @Test
    void incrementTaskCount_NonExistingColumn_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> dataStore.incrementTaskCount("non-existing", 5));
    }

    @Test
    void incrementTaskCount_UpdatesLastUpdatedTimestamp() {
        // Arrange
        Optional<BoardColumn> column = dataStore.findById("to-do");
        assertTrue(column.isPresent());
        LocalDateTime beforeIncrement = column.get().getLastUpdated();

        // Act
        dataStore.incrementTaskCount("to-do", 1);

        // Assert
        Optional<BoardColumn> updatedColumn = dataStore.findById("to-do");
        assertTrue(updatedColumn.isPresent());
        assertTrue(updatedColumn.get().getLastUpdated().isAfter(beforeIncrement) ||
                   updatedColumn.get().getLastUpdated().isEqual(beforeIncrement));
    }

    @Test
    void incrementTaskCount_MultipleIncrements_AccumulatesCorrectly() {
        // Arrange
        dataStore.incrementTaskCount("to-do", 5);
        dataStore.incrementTaskCount("to-do", 3);
        dataStore.incrementTaskCount("to-do", -2);

        // Act
        Optional<BoardColumn> column = dataStore.findById("to-do");

        // Assert
        assertTrue(column.isPresent());
        assertEquals(6, column.get().getTaskCount());
    }

    // ========== Integration Tests ==========

    @Test
    void fullCrudCycle_CreateReadUpdateIncrement_WorksCorrectly() {
        // Create
        BoardColumn column = new BoardColumn();
        column.setId("test-column");
        column.setName("Test Column");
        column.setTaskCount(0);
        column.setPosition(3);
        BoardColumn savedColumn = dataStore.save(column);
        assertNotNull(savedColumn);

        // Read
        Optional<BoardColumn> found = dataStore.findById("test-column");
        assertTrue(found.isPresent());
        assertEquals("Test Column", found.get().getName());

        // Update
        found.get().setName("Updated Column");
        BoardColumn updatedColumn = dataStore.save(found.get());
        assertEquals("Updated Column", updatedColumn.getName());

        // Increment
        dataStore.incrementTaskCount("test-column", 10);
        Optional<BoardColumn> incrementedColumn = dataStore.findById("test-column");
        assertTrue(incrementedColumn.isPresent());
        assertEquals(10, incrementedColumn.get().getTaskCount());
    }

    @Test
    void concurrentIncrements_AllColumnsIndependent() {
        // Arrange & Act
        dataStore.incrementTaskCount("to-do", 5);
        dataStore.incrementTaskCount("in-progress", 3);
        dataStore.incrementTaskCount("done", 7);

        // Assert
        Optional<BoardColumn> toDoColumn = dataStore.findById("to-do");
        Optional<BoardColumn> inProgressColumn = dataStore.findById("in-progress");
        Optional<BoardColumn> doneColumn = dataStore.findById("done");

        assertTrue(toDoColumn.isPresent());
        assertEquals(5, toDoColumn.get().getTaskCount());

        assertTrue(inProgressColumn.isPresent());
        assertEquals(3, inProgressColumn.get().getTaskCount());

        assertTrue(doneColumn.isPresent());
        assertEquals(7, doneColumn.get().getTaskCount());
    }
}