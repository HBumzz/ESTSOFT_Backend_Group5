package com.app.salty.board.service;

import com.app.salty.board.dto.islike.IsLikeRequestDto;
import com.app.salty.board.dto.islike.IsLikeResponseDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.IsLike;
import com.app.salty.board.entity.LikeContentType;
import com.app.salty.board.repository.IsLikeRepository;
import org.springframework.stereotype.Service;

@Service
public class IsLikeServiceImpl implements IsLikeService{

    IsLikeRepository isLikeRepository;
    ArticleServiceImpl articleService;
    CommentServiceImpl commentService;

    IsLikeServiceImpl(IsLikeRepository isLikeRepository
            , ArticleServiceImpl articleService
            , CommentServiceImpl commentService) {
        this.isLikeRepository = isLikeRepository;
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @Override
    public IsLikeResponseDto likeArticle(IsLikeRequestDto requestDto, Long articleId) {
        // 게시물 존재 여부 확인
        articleService.getArticleById(articleId);

        IsLike like = requestDto.toEntity();
        like.setContentId(articleId);
        return new IsLikeResponseDto(isLikeRepository.save(like));
    }

    @Override
    public IsLikeResponseDto likeComment(IsLikeRequestDto requestDto, Long commentId) {
        // 댓글 존재 여부 확인
        commentService.getCommentById(commentId);

        IsLike like = requestDto.toEntity();
        like.setContentId(commentId);
        return new IsLikeResponseDto(isLikeRepository.save(like));
    }

    @Override
    public Integer countLikesArticle(Long articleId) {
        LikeContentType contentType = LikeContentType.ARTICLE;
        return isLikeRepository.countIsLikesByContentTypeAndContentId(contentType,articleId);
    }

    @Override
    public Integer countLikesComment(Long commentId) {
        LikeContentType contentType = LikeContentType.COMMENT;
        return isLikeRepository.countIsLikesByContentTypeAndContentId(contentType,commentId);
    }

    @Override
    public void deleteLike(Long likeId) {
        IsLike isLike = isLikeRepository.findById(likeId).orElseThrow(IllegalArgumentException::new);
        isLikeRepository.delete(isLike);
    }
}
