package com.app.salty.checklist.controller;

import com.app.salty.checklist.dto.request.ChecklistItemRequestDTO;
import com.app.salty.checklist.dto.request.ChecklistItemUpdateDTO;
import com.app.salty.checklist.dto.response.ChecklistItemResponseDTO;
import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
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

    //체크리스트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ChecklistResponseDTO> getChecklist(
            @PathVariable Long userId,
            @RequestParam ChecklistType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(checklistService.getOrCreateChecklist(userId, type, date));
    }

    //체크리스트 항목 조회
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ChecklistItemResponseDTO> getChecklistItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(checklistService.getChecklistItem(itemId));
    }

    //체크리스트 항목 추가
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

    //체크리스트 항목 수정
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ChecklistItemResponseDTO> updateChecklistItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ChecklistItemUpdateDTO updateDTO) {
        return ResponseEntity.ok(checklistService.updateChecklistItem(itemId, updateDTO));
    }

    //체크리스트 항목 삭제
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteChecklistItem(@PathVariable Long itemId) {
        checklistService.deleteChecklistItem(itemId);
        return ResponseEntity.ok().build();
    }

    //체크리스트 항목 완료 토글
    @PatchMapping("/items/{itemId}/complete")
    public ResponseEntity<ChecklistItemResponseDTO> toggleItemCompletion(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(checklistService.toggleItemCompletion(itemId));
    }


}

