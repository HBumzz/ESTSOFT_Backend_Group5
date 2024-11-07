package com.app.salty.checklist.service;

import com.app.salty.checklist.dto.request.ChecklistItemRequestDTO;
import com.app.salty.checklist.dto.request.ChecklistItemUpdateDTO;
import com.app.salty.checklist.dto.request.ChecklistRequestDTO;
import com.app.salty.checklist.dto.response.ChecklistItemResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistSummaryDTO;
import com.app.salty.checklist.entity.*;
import com.app.salty.checklist.repository.CategoryRepository;
import com.app.salty.checklist.repository.ChecklistItemRepository;
import com.app.salty.checklist.repository.ChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ChecklistResponseDTO createChecklist(ChecklistRequestDTO requestDTO) {
        Checklist checklist = new Checklist();
        checklist.setUserId(requestDTO.getUserId());
        checklist.setTypeName(requestDTO.getTypeName());

        Checklist savedChecklist = checklistRepository.save(checklist);
        return convertToResponseDTO(savedChecklist);
    }

    @Transactional
    public ChecklistItemResponseDTO addChecklistItem(ChecklistItemRequestDTO requestDTO) {
        Checklist checklist = checklistRepository.findById(requestDTO.getChecklistId())
                .orElseThrow(() -> new RuntimeException("Checklist Not Found"));
        Category category = categoryRepository.findByCategoryType(requestDTO.getCategoryType())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ChecklistItem item = new ChecklistItem();
        item.setChecklist(checklist);
        item.setCategory(category);
        item.setItemContent(requestDTO.getItemContent());
        item.setItemMemo(requestDTO.getItemMemo());
        item.setSavedAmount(requestDTO.getSavedAmount());

        ChecklistItem savedItem = checklistItemRepository.save(item);
        updateCompletionRate(checklist);

        return convertToItemResponseDTO(savedItem);
    }

    public ChecklistItemResponseDTO updateChecklistItem(Long itemId, ChecklistItemUpdateDTO updateDTO) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item Not Found"));

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

        ChecklistItem updatedItem = checklistItemRepository.save(item);
        updateCompletionRate(updatedItem.getChecklist());

        return convertToItemResponseDTO(updatedItem);
    }

    private void updateCompletionRate(Checklist checklist) {
        long totalItems = checklistItemRepository.countByChecklist_ChecklistId(checklist.getChecklistId());
        long completedItems = checklistItemRepository.countCompletedItemsByChecklistId(checklist.getChecklistId());

        if (totalItems > 0) {
            BigDecimal completionRate = BigDecimal.valueOf(completedItems)
                    .divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            checklist.setCompletionRate(completionRate);
        } else {
            checklist.setCompletionRate(BigDecimal.ZERO);
        }
        checklistRepository.save(checklist);
    }

    public ChecklistResponseDTO getChecklist(Long userId, ChecklistType type, LocalDateTime date) {
        LocalDateTime startDate = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = date.withHour(23).withMinute(59).withSecond(59);

        List<Checklist> checklists = checklistRepository.findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(
                userId, type, startDate, endDate
        );

        if (checklists.isEmpty()) {
            return null;
        }

        return convertToResponseDTO(checklists.get(0));
    }

    private String formatDisplayDate(Checklist checklist) {
        LocalDateTime date = checklist.getChecklistCreatedAt();
        switch (checklist.getTypeName()) {
            case DAILY:
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case WEEKLY:
                WeekFields weekFields = WeekFields.of(Locale.getDefault()) ;
                int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                return String.format("%d년 %d월 %d주차", date.getYear(), date.getMonthValue(), weekNumber);
            case MONTHLY:
                return String.format("%d년 %d월", date.getYear(), date.getMonthValue());
            default:
                return "";
        }
    }

    private ChecklistResponseDTO convertToResponseDTO(Checklist checklist) {
        ChecklistResponseDTO responseDTO = new ChecklistResponseDTO();
        responseDTO.setChecklistId(checklist.getChecklistId());
        responseDTO.setTypename(checklist.getTypeName());
        responseDTO.setCompletionRate(checklist.getCompletionRate());
        responseDTO.setChecklistCreatedAt(checklist.getChecklistCreatedAt());
        responseDTO.setDisplayDate(formatDisplayDate(checklist));

        BigDecimal totalAmount = checklistItemRepository.calculateTotalAmountByChecklistId(checklist.getChecklistId());
        responseDTO.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

        List<ChecklistItemResponseDTO> itemDTOs = checklist.getItems().stream()
                .map(this::convertToItemResponseDTO)
                .collect(Collectors.toList());
        responseDTO.setItems(itemDTOs);

        return responseDTO;
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

    private long countByChecklistId(Long checklistId) {
        return checklistItemRepository.findByChecklist_ChecklistId(checklistId).size();
    }

    private ChecklistSummaryDTO convertToSummaryDTO(Checklist checklist) {
        ChecklistSummaryDTO summaryDTO = new ChecklistSummaryDTO();
        summaryDTO.setChecklistId(checklist.getChecklistId());
        summaryDTO.setPeriodDisplay(formatDisplayDate(checklist));
        summaryDTO.setCompletionRate(checklist.getCompletionRate());

        BigDecimal totalAmount = checklistItemRepository
                .calculateTotalAmountByChecklistId(checklist.getChecklistId());
        summaryDTO.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

        long totalItems = countByChecklistId(checklist.getChecklistId());
        long completedItems = checklistItemRepository
                .countCompletedItemsByChecklistId(checklist.getChecklistId());

        summaryDTO.setTotalItems((int) totalItems);
        summaryDTO.setCompletedItems((int) completedItems);

        return summaryDTO;
    }

    @Transactional
    public ChecklistItemResponseDTO toggleItemCompletion(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        item.setCompleted(!item.isCompleted());
        ChecklistItem updatedItem = checklistItemRepository.save(item);
        updateCompletionRate(updatedItem.getChecklist());

        return convertToItemResponseDTO(updatedItem);
    }

    @Transactional
    public void deleteChecklistItem(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        Checklist checklist = item.getChecklist();
        checklistItemRepository.deleteById(itemId);
        updateCompletionRate(checklist);
    }


    public ChecklistResponseDTO getChecklistByPeriod(Long userId, ChecklistType type,
                                                     int year, int month, Integer week) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (type == ChecklistType.WEEKLY && week != null) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            startDate = LocalDateTime.now()
                    .withYear(year)
                    .withMonth(month)
                    .with(weekFields.weekOfMonth(), week)
                    .with(weekFields.dayOfWeek(), 1)
                    .withHour(0).withMinute(0).withSecond(0);
            endDate = startDate.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        } else {
            startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
            endDate = startDate.plusMonths(1).minusSeconds(1);
        }

        List<Checklist> checklists = checklistRepository
                .findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(userId, type, startDate, endDate);

        return checklists.isEmpty() ? null : convertToResponseDTO(checklists.get(0));
    }

    @Transactional(readOnly = true)
    public List<ChecklistSummaryDTO> getChecklistSummary(Long userId, ChecklistType type,
                                                         LocalDateTime startDate, LocalDateTime endDate) {
        // 날짜 범위 정규화
        LocalDateTime normalizedStartDate = startDate.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime normalizedEndDate = endDate.withHour(23).withMinute(59).withSecond(59);

        // 정렬된 체크리스트 조회
        List<Checklist> checklists = checklistRepository
                .findByUserIdAndTypeNameAndDateRangeOrderByCreatedAtDesc(
                        userId, type, normalizedStartDate, normalizedEndDate);

        // 체크리스트가 없는 경우 빈 리스트 반환
        if (checklists.isEmpty()) {
            return Collections.emptyList();
        }

        // 각 체크리스트를 SummaryDTO로 변환
        return checklists.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }
}
