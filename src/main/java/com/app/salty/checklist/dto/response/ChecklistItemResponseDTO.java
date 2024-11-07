package com.app.salty.checklist.dto.response;

import com.app.salty.checklist.entity.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChecklistItemResponseDTO {
    private Long itemId;
    private String itemContent;
    private String itemMemo;
    private BigDecimal savedAmount;
    private CategoryType categoryType;
    private boolean isCompleted;
}
