package com.app.salty.checklist.controller;

import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.entity.ChecklistType;
import com.app.salty.checklist.service.ChecklistService;
import com.app.salty.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChecklistWebController {
    private final ChecklistService checklistService;


    // 체크리스트 페이지 표시
    @GetMapping("/checklist")
    public String showChecklist(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "DAILY") ChecklistType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            Model model) {

        LocalDateTime targetDate = determineTargetDate(type, date, year, month, week);
        ChecklistResponseDTO checklist = checklistService.getOrCreateChecklist(userId, type, targetDate);

        setModelAttributes(model, targetDate, type, checklist);

        return "checklist/checklist";
    }


    //타입별 날짜 결정
    private LocalDateTime determineTargetDate(
            ChecklistType type, LocalDate date, Integer year, Integer month, Integer week) {
        LocalDateTime now = LocalDateTime.now();

        if (type == ChecklistType.DAILY) {
            return date != null ? date.atStartOfDay() : now;
        }

        if (year != null && month != null) {
            if (type == ChecklistType.WEEKLY && week != null) {
                LocalDateTime firstDayOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
                LocalDateTime weekStart = firstDayOfMonth;
                while (weekStart.getDayOfWeek() != DayOfWeek.MONDAY) {
                    weekStart = weekStart.plusDays(1);
                }
                return weekStart.plusWeeks(week - 1);
            }
            if (type == ChecklistType.MONTHLY) {
                return LocalDateTime.of(year, month, 1, 0, 0);
            }
        }

        return now;
    }

    // 모델 속성 설정
    private void setModelAttributes(Model model, LocalDateTime date, ChecklistType type, ChecklistResponseDTO checklist) {
        // 기본 속성 설정
        model.addAttribute("userName", "사용자");
        model.addAttribute("currentMonth", date.getMonthValue());
        model.addAttribute("currentDate", DateUtils.formatDisplayDate(date, type));
        model.addAttribute("types", ChecklistType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("targetDate", date);

        // 체크리스트 관련 속성 설정
        if (checklist != null) {
            model.addAttribute("checklistId", checklist.getChecklistId());
            model.addAttribute("totalAmount", checklist.getTotalAmount());
            model.addAttribute("completionRate", checklist.getCompletionRate());
            model.addAttribute("checklistItems", checklist.getItems());
            model.addAttribute("savingTitle", checklist.getSavingTitle());
        } else {
            model.addAttribute("checklistId", null);
            model.addAttribute("totalAmount", BigDecimal.ZERO);
            model.addAttribute("completionRate", 0);
            model.addAttribute("checklistItems", Collections.emptyList());
            model.addAttribute("savingTitle", "님의 절약 금액");
        }
    }
}
