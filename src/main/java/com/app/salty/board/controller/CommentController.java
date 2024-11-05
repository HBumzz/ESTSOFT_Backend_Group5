package com.app.salty.board.controller;

import com.app.salty.board.dto.comment.*;
import com.app.salty.board.service.CommentServiceImpl;
import com.app.salty.user.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    CommentServiceImpl commentService;

    CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    // 전체 댓글 불러오기
    @GetMapping("/comment")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentList() {
        List<GetCommentResponseDto> dtoList = commentService.getCommentList();
        return ResponseEntity.ok(dtoList);
    }

    // commentId로 댓글 조회
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<GetCommentResponseDto> getCommentBy(@PathVariable Long commentId) {
        GetCommentResponseDto dto = commentService.getCommentById(commentId);
        return ResponseEntity.ok(dto);
    }

    // article에 댓글 달기
    @PostMapping("/comment/{articleId}")
    public ResponseEntity<SaveCommentResponseDto> saveComment(
            @RequestBody SaveCommentRequestDto requestDto
            , @PathVariable Long articleId
            , @AuthenticationPrincipal Users user) {

        // 임의의 유저 생성 - test
        Users tempUser = new Users();
        tempUser.setId(1L);
        // ==============================
        requestDto.setUser(tempUser);
        SaveCommentResponseDto responseDto = commentService.saveComment(requestDto,articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 댓글(commentId) 수정하기
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<UpdateCommentResponseDto> updateComment(
            @PathVariable Long commentId
            , @RequestBody UpdateCommentRequestDto requestDto) {

        UpdateCommentResponseDto responseDto = commentService.updateComment(requestDto, commentId);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글(commentId) 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // article(articleId)의 댓글 전체 조회하기
    @GetMapping("/comment/article/{articleId}")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<GetCommentResponseDto> responseDto = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(responseDto);
    }
    // user의 댓글 전체 조회
    @GetMapping("/comment/user/{userId}")
    public ResponseEntity<List<GetCommentResponseDto>> getCommentsByUserId(@PathVariable Long userId) {
        List<GetCommentResponseDto> responseDto = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(responseDto);
    }
}
