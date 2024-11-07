package com.app.salty.checklist.controller;

import com.app.salty.checklist.dto.request.ChecklistRequestDTO;
import com.app.salty.checklist.dto.response.ChecklistResponseDTO;
import com.app.salty.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    @PostMapping
    public ResponseEntity<ChecklistResponseDTO> createChecklist(@Valid @RequestBody ChecklistRequestDTO requestDTO) {
        return ResponseEntity.ok(checklistService.createChecklist(requestDTO));
    }


}
