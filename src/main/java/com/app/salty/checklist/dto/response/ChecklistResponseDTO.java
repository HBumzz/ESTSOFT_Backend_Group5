package com.app.salty.checklist.dto.response;

import com.app.salty.checklist.entity.ChecklistType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class ChecklistResponseDTO {
    private Long checklistId;
    private ChecklistType typename;
    private Integer completionRate;
    private BigDecimal totalAmount;
    private List<ChecklistItemResponseDTO> items;
    private LocalDateTime checklistCreatedAt;
    private String displayDate;
    private String savingTitle;
}
