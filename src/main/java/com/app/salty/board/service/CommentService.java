package com.app.salty.board.service;

import com.app.salty.board.dto.comment.*;
import com.app.salty.board.entity.ArticleType;
import com.app.salty.user.entity.Users;

import java.util.List;

public interface CommentService {
    List<GetCommentResponseDto> getCommentList();
    GetCommentResponseDto getCommentById(Long commentId);
    SaveCommentResponseDto saveComment(SaveCommentRequestDto dto, Long articleId);
    UpdateCommentResponseDto updateComment(UpdateCommentRequestDto dto, Long commentId);
    void deleteComment(Long commentId);

    List<GetCommentResponseDto> getCommentsByArticleId(Long articleId);

    List<GetCommentResponseDto> getCommentsByUserId(Long userId);
}
