package com.app.salty.checklist.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
public class ChecklistItemUpdateDTO {
    @Size(max = 50)
    private String itemContent;

    @Size(max = 100)
    private String itemMemo;

    private BigDecimal savedAmount;
    private Boolean isCompleted;
}
