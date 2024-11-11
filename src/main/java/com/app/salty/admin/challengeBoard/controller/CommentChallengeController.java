package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.request.CommentRequestDTO;
import com.app.salty.admin.challengeBoard.dto.response.CommentResponseDTO;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.service.ChallengeCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentChallengeController {
    private final ChallengeCommentService challengeCommentService;

    public CommentChallengeController(ChallengeCommentService challengeCommentService) {
        this.challengeCommentService = challengeCommentService;
    }

    @PostMapping(name = "/api/articles/{articleId}/comments")
    public ResponseEntity<CommentResponseDTO> saveCommentByArticleId(@PathVariable Long articleId,
                                                                     @RequestBody CommentRequestDTO request) {
        ChallengeComment comment = challengeCommentService.saveComment(articleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponseDTO(comment));
    }

    @GetMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> selectCommentById(@PathVariable("commentId") Long id) {
        ChallengeComment comment = challengeCommentService.findComment(id);
        return ResponseEntity.ok(new CommentResponseDTO(comment));
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateCommentById(@PathVariable Long commentId,
                                                                @RequestBody CommentRequestDTO request) {
        ChallengeComment updated = challengeCommentService.update(commentId, request);
        return ResponseEntity.ok(new CommentResponseDTO(updated));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long commentId) {
        challengeCommentService.delete(commentId);
        return ResponseEntity.ok().build();
    }
}
