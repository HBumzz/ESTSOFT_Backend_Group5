package com.app.salty.board.service;

import com.app.salty.board.dto.islike.IsLikeRequestDto;
import com.app.salty.board.dto.islike.IsLikeResponseDto;

public interface IsLikeService {
    IsLikeResponseDto likeArticle(IsLikeRequestDto requestDto, Long articleId);
    IsLikeResponseDto likeComment(IsLikeRequestDto requestDto, Long commentId);
    Integer countLikesArticle(Long articleId);
    Integer countLikesComment(Long commentId);
    void deleteLike(Long likeId);
}
