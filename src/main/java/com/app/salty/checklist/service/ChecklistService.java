package com.app.salty.checklist.service;

import com.app.salty.checklist.dto.request.ChecklistItemRequestDTO;
import com.app.salty.checklist.dto.request.ChecklistItemUpdateDTO;
import com.app.salty.checklist.dto.request.ChecklistRequestDTO;
import com.app.salty.checklist.dto.response.ChecklistItemResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.entity.*;
import com.app.salty.checklist.repository.CategoryRepository;
import com.app.salty.checklist.repository.ChecklistItemRepository;
import com.app.salty.checklist.repository.ChecklistRepository;
import com.app.salty.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CategoryRepository categoryRepository;
    private final DateUtils dateUtils;

    @Transactional
    public ChecklistResponseDTO getOrCreateChecklist(Long userId, ChecklistType type, LocalDateTime targetDate) {
        LocalDateTime startDate = DateUtils.getStartDate(type, targetDate);
        LocalDateTime endDate = DateUtils.getEndDate(type, targetDate);

        List<Checklist> existingChecklists = checklistRepository
                .findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(userId, type, startDate, endDate);

        if (!existingChecklists.isEmpty()) {
            return convertToResponseDTO(existingChecklists.get(0));
        }

        if (DateUtils.isPastDate(type, targetDate)) {
            Checklist emptyChecklist = new Checklist();
            emptyChecklist.setUserId(userId);
            emptyChecklist.setTypeName(type);
            emptyChecklist.setChecklistCreatedAt(targetDate);
            emptyChecklist.setCompletionRate(0);
            return convertToResponseDTO(emptyChecklist);
        }

        Checklist newChecklist = new Checklist();
        newChecklist.setUserId(userId);
        newChecklist.setTypeName(type);
        newChecklist.setChecklistCreatedAt(targetDate);
        newChecklist.setCompletionRate(0);

        return convertToResponseDTO(checklistRepository.save(newChecklist));
    }

    private Checklist createNewChecklist(Long userId, ChecklistType type) {
        Checklist newChecklist = new Checklist();
        newChecklist.setUserId(userId);
        newChecklist.setTypeName(type);
        newChecklist.setCompletionRate(0);  // BigDecimal.ZERO 대신 0 사용
        return checklistRepository.save(newChecklist);
    }

    @Transactional
    public ChecklistItemResponseDTO addChecklistItem(ChecklistItemRequestDTO requestDTO) {
        Checklist checklist = checklistRepository.findById(requestDTO.getChecklistId())
                .orElseThrow(() -> new RuntimeException("Checklist Not Found"));

        if (DateUtils.isPastDate(checklist.getTypeName(), checklist.getChecklistCreatedAt())) {
            throw new IllegalStateException("Cannot modify past checklist items");
        }

        Category category = categoryRepository.findByCategoryType(requestDTO.getCategoryType())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ChecklistItem item = createChecklistItem(requestDTO, checklist, category);
        ChecklistItem savedItem = checklistItemRepository.save(item);
        updateCompletionRate(checklist);

        return convertToItemResponseDTO(savedItem);
    }

    @Transactional
    public ChecklistItemResponseDTO toggleItemCompletion(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        item.setCompleted(!item.isCompleted());
        ChecklistItem updatedItem = checklistItemRepository.save(item);
        updateCompletionRate(item.getChecklist());

        return convertToItemResponseDTO(updatedItem);
    }

    private void updateCompletionRate(Checklist checklist) {
        long totalItems = checklistItemRepository.countByChecklist_ChecklistId(checklist.getChecklistId());
        long completedItems = checklistItemRepository.countCompletedItemsByChecklistId(checklist.getChecklistId());

        int completionRate = totalItems > 0
                ? (int) Math.round((double) completedItems * 100 / totalItems)
                : 0;

        checklist.setCompletionRate(completionRate);
        checklistRepository.save(checklist);
    }

    private String getSavingTitle(ChecklistType type, LocalDateTime date) {
        return switch (type) {
            case DAILY -> "님의 오늘 절약 금액";
            case WEEKLY -> "님의 이번 주 절약 금액";
            case MONTHLY -> "님의 " + date.getMonthValue() + "월 절약 금액";
        };
    }

    private ChecklistResponseDTO convertToResponseDTO(Checklist checklist) {
        ChecklistResponseDTO dto = new ChecklistResponseDTO();
        dto.setChecklistId(checklist.getChecklistId());
        dto.setTypename(checklist.getTypeName());
        dto.setChecklistCreatedAt(checklist.getChecklistCreatedAt());
        dto.setDisplayDate(formatDisplayDate(checklist));
        dto.setCompletionRate(checklist.getCompletionRate());  // Integer로 자동 변환
        dto.setSavingTitle(getSavingTitle(checklist.getTypeName(), checklist.getChecklistCreatedAt()));
        dto.setTotalAmount(calculateTotalAmount(
                checklist.getUserId(),
                checklist.getTypeName(),
                checklist.getChecklistCreatedAt()));
        dto.setItems(checklist.getItems().stream()
                .map(this::convertToItemResponseDTO)
                .collect(Collectors.toList()));

        log.debug("Converting checklist to DTO - Type: {}, Title: {}",
                checklist.getTypeName(), dto.getSavingTitle());

        return dto;
    }


    private ChecklistItem createChecklistItem(ChecklistItemRequestDTO requestDTO, Checklist checklist, Category category) {
        ChecklistItem item = new ChecklistItem();
        item.setChecklist(checklist);
        item.setCategory(category);
        item.setItemContent(requestDTO.getItemContent());
        item.setItemMemo(requestDTO.getItemMemo());
        item.setSavedAmount(requestDTO.getSavedAmount());
        return item;
    }


    private ChecklistItemResponseDTO convertToItemResponseDTO(ChecklistItem item) {
        ChecklistItemResponseDTO dto = new ChecklistItemResponseDTO();
        dto.setItemId(item.getItemId());
        dto.setItemContent(item.getItemContent());
        dto.setItemMemo(item.getItemMemo());
        dto.setSavedAmount(item.getSavedAmount());
        dto.setCategoryType(item.getCategory().getCategoryType());
        dto.setCompleted(item.isCompleted());
        return dto;
    }

    private String formatDisplayDate(Checklist checklist) {
        LocalDateTime date = checklist.getChecklistCreatedAt();
        return switch (checklist.getTypeName()) {
            case DAILY -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case WEEKLY -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                yield String.format("%d년 %d월 %d주차",
                        date.getYear(), date.getMonthValue(), weekNumber);
            }
            case MONTHLY -> String.format("%d년 %d월", date.getYear(), date.getMonthValue());
        };
    }

    @Transactional
    public ChecklistResponseDTO createChecklist(ChecklistRequestDTO requestDTO) {
        Checklist checklist = new Checklist();
        checklist.setUserId(requestDTO.getUserId());
        checklist.setTypeName(requestDTO.getTypeName());
        checklist.setCompletionRate(0);  // BigDecimal.ZERO 대신 0 사용

        Checklist savedChecklist = checklistRepository.save(checklist);
        return convertToResponseDTO(savedChecklist);
    }

    public ChecklistItemResponseDTO getChecklistItem(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));
        return convertToItemResponseDTO(item);
    }

    @Transactional
    public ChecklistItemResponseDTO updateChecklistItem(Long itemId, ChecklistItemUpdateDTO updateDTO) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        // 과거 데이터 수정 방지
        if (isPastDate(item.getChecklist().getTypeName(), item.getChecklist().getChecklistCreatedAt())) {
            throw new IllegalStateException("Cannot modify past checklist items");
        }

        updateItemFields(item, updateDTO);
        ChecklistItem savedItem = checklistItemRepository.save(item);
        updateCompletionRate(item.getChecklist());

        return convertToItemResponseDTO(savedItem);
    }

    private void updateItemFields(ChecklistItem item, ChecklistItemUpdateDTO updateDTO) {
        if (updateDTO.getItemContent() != null) {
            item.setItemContent(updateDTO.getItemContent());
        }
        if (updateDTO.getItemMemo() != null) {
            item.setItemMemo(updateDTO.getItemMemo());
        }
        if (updateDTO.getSavedAmount() != null) {
            item.setSavedAmount(updateDTO.getSavedAmount());
        }
        if (updateDTO.getIsCompleted() != null) {
            item.setCompleted(updateDTO.getIsCompleted());
        }
    }

    @Transactional
    public void deleteChecklistItem(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        Checklist checklist = item.getChecklist();
        checklistItemRepository.deleteById(itemId);
        updateCompletionRate(checklist);
    }


    private BigDecimal calculateTotalAmount(Long userId, ChecklistType type, LocalDateTime currentDate) {
        LocalDateTime startDate, endDate;

        switch (type) {
            case DAILY -> {
                startDate = currentDate.withHour(0).withMinute(0).withSecond(0);
                endDate = currentDate.withHour(23).withMinute(59).withSecond(59);
            }
            case WEEKLY -> {
                startDate = currentDate.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
                endDate = startDate.plusDays(6).withHour(23).withMinute(59).withSecond(59);
            }
            case MONTHLY -> {
                startDate = currentDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            }
            default -> throw new IllegalArgumentException("Invalid checklist type");
        }

        List<Checklist> checklists = checklistRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return checklists.stream()
                .flatMap(checklist -> checklist.getItems().stream())
                .filter(ChecklistItem::isCompleted)
                .map(ChecklistItem::getSavedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Checklist> findByUserIdAndTypeAndDate(Long userId, ChecklistType type, LocalDateTime targetDate) {
        LocalDateTime startDate = targetDate.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = targetDate.withHour(23).withMinute(59).withSecond(59);

        // 기존 repository 메서드 사용
        return checklistRepository.findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(
                userId, type, startDate, endDate);
    }



    // 현재 시점 기준으로 과거 날짜인지 확인
    private boolean isPastDate(ChecklistType type, LocalDateTime targetDate) {
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

    private LocalDateTime getStartDate(ChecklistType type, LocalDateTime date) {
        return switch (type) {
            case DAILY -> date.toLocalDate().atStartOfDay();
            case WEEKLY -> date.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            case MONTHLY -> date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        };
    }

    private LocalDateTime getEndDate(ChecklistType type, LocalDateTime date) {
        return switch (type) {
            case DAILY -> date.toLocalDate().atTime(23, 59, 59);
            case WEEKLY -> date.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);
            case MONTHLY -> date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
                    .toLocalDate().atTime(23, 59, 59);
        };
    }



}