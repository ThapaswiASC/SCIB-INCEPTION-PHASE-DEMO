package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId")
    Long countTasksByUserId(@Param("userId") Long userId);
    
    Page<Task> findByUserId(Long userId, Pageable pageable);
    
    List<Task> findByColumnId(String columnId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.columnId = :columnId")
    Long countByColumnId(@Param("columnId") String columnId);
}
