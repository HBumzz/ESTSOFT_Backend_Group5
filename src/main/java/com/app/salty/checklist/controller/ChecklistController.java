package com.app.salty.checklist.controller;

import com.app.salty.checklist.dto.request.ChecklistItemRequestDTO;
import com.app.salty.checklist.dto.request.ChecklistItemUpdateDTO;
import com.app.salty.checklist.dto.request.ChecklistRequestDTO;
import com.app.salty.checklist.dto.response.ChecklistItemResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistSummaryDTO;
import com.app.salty.checklist.entity.ChecklistType;
import com.app.salty.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

//    @PostMapping
//    public ResponseEntity<ChecklistResponseDTO> createChecklist(@Valid @RequestBody ChecklistRequestDTO requestDTO) {
//        return ResponseEntity.ok(checklistService.createChecklist(requestDTO));
//    }

    @GetMapping("/{userId}")
    public ResponseEntity<ChecklistResponseDTO> getChecklist(
            @PathVariable Long userId,
            @RequestParam ChecklistType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(checklistService.getChecklist(userId, type, date));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ChecklistItemResponseDTO> getChecklistItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(checklistService.getChecklistItem(itemId));
    }

    @PostMapping("/items")
    public ResponseEntity<?> addChecklistItem(
            @Valid @RequestBody ChecklistItemRequestDTO requestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            ChecklistItemResponseDTO responseDTO = checklistService.addChecklistItem(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ChecklistItemResponseDTO> updateChecklistItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ChecklistItemUpdateDTO updateDTO) {
        return ResponseEntity.ok(checklistService.updateChecklistItem(itemId, updateDTO));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteChecklistItem(@PathVariable Long itemId) {
        checklistService.deleteChecklistItem(itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<List<ChecklistSummaryDTO>> getChecklistSummary(
            @PathVariable Long userId,
            @RequestParam ChecklistType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(checklistService.getChecklistSummary(userId, type, startDate, endDate));
    }

    @PatchMapping("/items/{itemId}/complete")
    public ResponseEntity<ChecklistItemResponseDTO> toggleItemCompletion(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(checklistService.toggleItemCompletion(itemId));
    }


}

