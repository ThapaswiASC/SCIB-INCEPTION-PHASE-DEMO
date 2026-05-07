package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<Column, String> {

    @Modifying
    @Query("UPDATE Column c SET c.taskCount = c.taskCount + :increment WHERE c.id = :columnId")
    int incrementTaskCount(@Param("columnId") String columnId, @Param("increment") int increment);

    Optional<Column> findById(String columnId);
}