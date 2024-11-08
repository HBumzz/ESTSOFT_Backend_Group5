package com.app.salty.board.controller;

import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.board.service.LikeServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LikeController {

    LikeServiceImpl likeService;
    ArticleRepository articleRepository;
    CommentRepository commentRepository;

    LikeController(LikeServiceImpl likeService
            , ArticleRepository articleRepository
            , CommentRepository commentRepository) {
        this.likeService=likeService;
        this.articleRepository=articleRepository;
        this.commentRepository=commentRepository;
    }

    @GetMapping("/article/like/{articleId}")
    public ResponseEntity<Void> likeArticle(@PathVariable Long articleId
            , @AuthenticationPrincipal UserDetails user) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.ARTICLE);
        requestDto.setContentId(articleId);
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        requestDto.setArticle(article);

        // 임시
        requestDto.setUser_id(1L);

        likeService.Like(requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/like/{commentId}")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId
            ,@AuthenticationPrincipal UserDetails user) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        requestDto.setContentId(commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);

        // 임시
        requestDto.setUser_id(1L);

        likeService.Like(requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/article/like/count/{articleId}")
    public ResponseEntity<Integer> CountArticleLike(@PathVariable Long articleId) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.ARTICLE);
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        requestDto.setArticle(article);
        requestDto.setUser_id(1L);

        Integer count = likeService.countLike(requestDto);

        return ResponseEntity.ok(count);
    }

    @GetMapping("/comment/like/count/{commentId}")
    public ResponseEntity<Integer> CountCommentLike(@PathVariable Long commentId) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);
        requestDto.setUser_id(1L);

        Integer count = likeService.countLike(requestDto);

        return ResponseEntity.ok(count);
    }
}
