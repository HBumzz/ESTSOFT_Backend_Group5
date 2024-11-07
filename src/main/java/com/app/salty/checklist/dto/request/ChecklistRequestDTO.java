package com.app.salty.checklist.dto.request;

import com.app.salty.checklist.entity.ChecklistType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChecklistRequestDTO {
    @NotNull
    private Long userId;
    @NotNull
    private ChecklistType typeName;
    private LocalDateTime targetDate;
    private Integer weekNumber;
    private Integer monthNumber;
}
