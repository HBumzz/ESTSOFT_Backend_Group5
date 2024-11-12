package com.app.salty.checklist.repository;

import com.app.salty.checklist.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    long countByChecklist_ChecklistId(Long checklistId);

    @Query("SELECT COUNT(ci) FROM ChecklistItem ci " +
            "WHERE ci.checklist.checklistId = :checklistId " +
            "AND ci.isCompleted = true")
    long countCompletedItemsByChecklistId(@Param("checklistId") Long checklistId);



}
