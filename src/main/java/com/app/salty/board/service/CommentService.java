package com.app.salty.board.service;

import com.app.salty.board.dto.comment.*;

import java.util.List;

public interface CommentService {
    List<GetCommentResponseDto> getCommentList();
    GetCommentResponseDto getCommentById(Long id);
    SaveCommentResponseDto saveComment(SaveCommentRequestDto dto, Long articleId);
    UpdateCommentResponseDto updateComment(UpdateCommentRequestDto dto, Long commentId);
    void deleteComment(Long id);

    List<GetCommentResponseDto> getCommentsByArticleId(Long articleId);

    List<GetCommentResponseDto> getCommentsByUserId(Long userId);
}
