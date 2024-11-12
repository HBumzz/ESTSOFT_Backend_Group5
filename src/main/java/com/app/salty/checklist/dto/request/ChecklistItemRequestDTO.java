package com.app.salty.checklist.dto.request;

import com.app.salty.checklist.entity.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChecklistItemRequestDTO {
    @NotNull
    private Long checklistId;

    @NotNull @Size(min = 1, max = 50)
    private String itemContent;

    @Size(max = 100)
    private String itemMemo;

    @NotNull
    private BigDecimal savedAmount;

    @NotNull
    private CategoryType categoryType;
}
