package com.app.salty.checklist.controller;

import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.entity.ChecklistType;
import com.app.salty.checklist.service.ChecklistService;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChecklistWebController {
    private final ChecklistService checklistService;

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

        setBaseModelAttributes(model, targetDate, type);
        setChecklistModelAttributes(model, checklist);

        return "checklist/checklist";
    }


    private LocalDateTime determineTargetDate(
            ChecklistType type, LocalDate date, Integer year, Integer month, Integer week) {
        LocalDateTime now = LocalDateTime.now();

        // 각 타입별 날짜 결정 로직
        if (type == ChecklistType.DAILY) {
            return date != null ? date.atStartOfDay() : now;
        }

        if (year != null && month != null) {
            if (type == ChecklistType.WEEKLY && week != null) {
                // 주차 계산 로직
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

    private void setBaseModelAttributes(Model model, LocalDateTime date, ChecklistType type) {
        model.addAttribute("userName", "사용자");
        model.addAttribute("currentMonth", date.getMonthValue());
        model.addAttribute("currentDate", formatDisplayDate(date, type));
        model.addAttribute("types", ChecklistType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("targetDate", date); // 추가
    }

    private String formatDisplayDate(LocalDateTime date, ChecklistType type) {
        return switch (type) {
            case DAILY -> date.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN));
            case WEEKLY -> {
                LocalDateTime firstDayOfMonth = date.withDayOfMonth(1);
                LocalDateTime targetMonday = date;

                // 해당 날짜의 월요일 찾기
                while (targetMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
                    targetMonday = targetMonday.minusDays(1);
                }

                // 첫 번째 월요일 찾기
                LocalDateTime firstMonday = firstDayOfMonth;
                while (firstMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
                    firstMonday = firstMonday.plusDays(1);
                }

                // 주차 계산
                long weekNumber = ChronoUnit.WEEKS.between(firstMonday, targetMonday) + 1;

                yield targetMonday.format(DateTimeFormatter.ofPattern("M월 ")) + weekNumber + "주차";
            }
            case MONTHLY -> date.format(DateTimeFormatter.ofPattern("M월"));
        };
    }

    private void setChecklistModelAttributes(Model model, ChecklistResponseDTO checklist) {
        if (checklist != null) {
            model.addAttribute("checklistId", checklist.getChecklistId());
            model.addAttribute("totalAmount", checklist.getTotalAmount());
            model.addAttribute("completionRate", checklist.getCompletionRate());
            model.addAttribute("checklistItems", checklist.getItems());
            // savingTitle 추가
            model.addAttribute("savingTitle", checklist.getSavingTitle());
        } else {
            model.addAttribute("checklistId", null);
            model.addAttribute("totalAmount", BigDecimal.ZERO);
            model.addAttribute("completionRate", BigDecimal.ZERO);
            model.addAttribute("checklistItems", Collections.emptyList());
            model.addAttribute("savingTitle", "님의 절약 금액");
        }
    }

    private String formatCurrentDate(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN));
    }
}
