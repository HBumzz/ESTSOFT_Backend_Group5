package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.request.CommentRequestDTO;
import com.app.salty.admin.challengeBoard.dto.response.CommentResponseDTO;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.service.ChallengeCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@RestController
@RequestMapping("api/chboard/{challengeId}/comments")
public class CommentChallengeController {
    private final ChallengeCommentService challengeCommentService;

    public CommentChallengeController(ChallengeCommentService challengeCommentService) {
        this.challengeCommentService = challengeCommentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> saveComment(@PathVariable Long challengeId,
                                                          @RequestBody CommentRequestDTO request) {
        ChallengeComment comment = challengeCommentService.saveComment(challengeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponseDTO(comment));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getComment(@PathVariable Long commentId) {
        ChallengeComment comment = challengeCommentService.findComment(commentId);
        return ResponseEntity.ok(new CommentResponseDTO(comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable Long commentId,
                                                            @RequestBody CommentRequestDTO request) {
        ChallengeComment updated = challengeCommentService.update(commentId, request);
        return ResponseEntity.ok(new CommentResponseDTO(updated));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @PathVariable Long challengeId) {
        challengeCommentService.delete(commentId);
        return ResponseEntity.ok().build();
    }
}
