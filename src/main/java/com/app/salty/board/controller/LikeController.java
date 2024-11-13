package com.app.salty.board.controller;

import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.board.service.LikeServiceImpl;
import com.app.salty.user.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Salty - 게시판(게시글, 댓글) 좋아요 관련 API")
@RestController
@RequestMapping("api")
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
    @Operation(summary = "게시글(articleId) 좋아요")
    @GetMapping("/article/like/{articleId}")
    public ResponseEntity<Void> likeArticle(@PathVariable Long articleId
            , @AuthenticationPrincipal CustomUserDetails user) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.ARTICLE);
        requestDto.setContentId(articleId);
        requestDto.setUser_id(user.getId());
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        requestDto.setArticle(article);

        likeService.Like(requestDto);

        return ResponseEntity.ok().build();
    }
    @Operation(summary = "뎃글(commentId) 좋아요")
    @GetMapping("/comment/like/{commentId}")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId
            ,@AuthenticationPrincipal CustomUserDetails user) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        requestDto.setContentId(commentId);
        requestDto.setUser_id(user.getId());
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);

        likeService.Like(requestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글(articleId) 좋아요 갯수")
    @GetMapping("/article/like/count/{articleId}")
    public ResponseEntity<Integer> CountArticleLike(@PathVariable Long articleId
            ,@AuthenticationPrincipal CustomUserDetails user ) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.ARTICLE);
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        requestDto.setArticle(article);
        requestDto.setUser_id(user.getId());

        Integer count = likeService.countLike(requestDto);

        return ResponseEntity.ok(count);
    }

    @Operation(summary = "댓글(commentId) 좋아요 갯수")
    @GetMapping("/comment/like/count/{commentId}")
    public ResponseEntity<Integer> CountCommentLike(@PathVariable Long commentId
            ,@AuthenticationPrincipal CustomUserDetails user) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);
        requestDto.setUser_id(user.getId());

        Integer count = likeService.countLike(requestDto);

        return ResponseEntity.ok(count);
    }
}
