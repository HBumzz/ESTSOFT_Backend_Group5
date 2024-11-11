package com.app.salty.checklist.repository;

import com.app.salty.checklist.entity.Checklist;
import com.app.salty.checklist.entity.ChecklistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    Optional<Checklist> findByUserIdAndTypeName(Long userId, ChecklistType typeName);

    @Query("SELECT c FROM Checklist c " +
           "WHERE c.userId = :userId " +
           "AND c.typeName = :type " +
           "AND c.checklistCreatedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY c.checklistCreatedAt DESC")

    List<Checklist> findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("type") ChecklistType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
