package com.app.salty.board.controller;

import com.app.salty.board.dto.comment.*;
import com.app.salty.board.entity.ArticleType;
import com.app.salty.board.service.CommentServiceImpl;
import com.app.salty.user.entity.Users;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.valueOf;
@Tag(name = "Salty - 댓글 관련 API")
@RestController
@RequestMapping("/api")
public class CommentController {

    CommentServiceImpl commentService;

    CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    // 전체 댓글 불러오기
    @Operation(summary = "댓글 전체 조회")
    @GetMapping("/comment")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentList() {
        List<GetCommentResponseDto> dtoList = commentService.getCommentList();
        return ResponseEntity.ok(dtoList);
    }

    // 댓글 조회
    @Operation(summary = "댓글 단건 조회")
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<GetCommentResponseDto> getCommentBy(@PathVariable Long commentId) {
        GetCommentResponseDto dto = commentService.getCommentById(commentId);
        return ResponseEntity.ok(dto);
    }

    // 댓글(commentId) 수정하기
    @Operation(summary = "댓글 수정")
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<UpdateCommentResponseDto> updateComment(@RequestBody UpdateCommentRequestDto requestDto
            , @PathVariable Long commentId) {

        UpdateCommentResponseDto responseDto = commentService.updateComment(requestDto, commentId);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글(commentId) 삭제
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // article(articleId)의 댓글 전체 조회하기
    @Operation(summary = "해당 게시글(articleId)의 댓글 전체 조회")
    @GetMapping("/comment/article/{articleId}")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<GetCommentResponseDto> responseDto = commentService.getCommentsByArticleId(articleId);


        return ResponseEntity.ok(responseDto);
    }
    // user의 댓글 전체 조회
    @Hidden
    @GetMapping("/comment/user/{userId}")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentsByUserId(@PathVariable Long userId) {
        List<GetCommentResponseDto> responseDto = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(responseDto);
    }
}
