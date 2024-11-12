package com.app.salty.util;

import com.app.salty.checklist.entity.ChecklistType;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class DateUtils {
    public static String formatDisplayDate(LocalDateTime date, ChecklistType type) {
        return switch (type) {
            case DAILY -> date.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN));
            case WEEKLY -> {
                LocalDateTime firstDayOfMonth = date.withDayOfMonth(1);
                LocalDateTime targetMonday = date;
                while (targetMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
                    targetMonday = targetMonday.minusDays(1);
                }
                LocalDateTime firstMonday = firstDayOfMonth;
                while (firstMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
                    firstMonday = firstMonday.plusDays(1);
                }
                long weekNumber = ChronoUnit.WEEKS.between(firstMonday, targetMonday) + 1;
                yield targetMonday.format(DateTimeFormatter.ofPattern("M월 ")) + weekNumber + "주차";
            }
            case MONTHLY -> date.format(DateTimeFormatter.ofPattern("M월"));
        };
    }

    public static boolean isPastDate(ChecklistType type, LocalDateTime targetDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();

        return switch (type) {
            case DAILY -> targetDate.isBefore(startOfToday);
            case WEEKLY -> {
                LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
                yield targetDate.isBefore(startOfWeek);
            }
            case MONTHLY -> {
                LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                yield targetDate.isBefore(startOfMonth);
            }
        };
    }

    public static LocalDateTime getStartDate(ChecklistType type, LocalDateTime date) {
        return switch (type) {
            case DAILY -> date.toLocalDate().atStartOfDay();
            case WEEKLY -> date.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            case MONTHLY -> date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        };
    }

    public static LocalDateTime getEndDate(ChecklistType type, LocalDateTime date) {
        return switch (type) {
            case DAILY -> date.toLocalDate().atTime(23, 59, 59);
            case WEEKLY -> date.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);
            case MONTHLY -> date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
                    .toLocalDate().atTime(23, 59, 59);
        };
    }
}
