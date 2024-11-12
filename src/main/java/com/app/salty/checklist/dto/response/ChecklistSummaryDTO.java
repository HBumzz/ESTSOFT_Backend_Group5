package com.app.salty.checklist.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class ChecklistSummaryDTO {

    private Long checklistId;
    private String periodDisplay;
    private Integer completionRate;
    private BigDecimal totalAmount;
    private int totalItems;
    private int completedItems;
}
