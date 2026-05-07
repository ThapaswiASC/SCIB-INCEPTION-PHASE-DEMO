package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.userId = :userId")
    Page<Task> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.columnId = :columnId")
    List<Task> findByColumnId(@Param("columnId") String columnId);

    @Query("SELECT t FROM Task t WHERE t.id = :taskId")
    Optional<Task> findTaskById(@Param("taskId") Long taskId);

    List<Task> findByTitleContainingIgnoreCase(String title);
}