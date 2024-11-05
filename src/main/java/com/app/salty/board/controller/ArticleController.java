package com.app.salty.board.controller;

import com.app.salty.board.dto.article.*;
import com.app.salty.board.service.ArticleServiceImpl;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ArticleController {
    ArticleServiceImpl articleService;
    UserService userService;

    ArticleController(ArticleServiceImpl articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    // 게시물 전체 조회
    @GetMapping("/article")
    public ResponseEntity<List<GetArticleResponseDto>> getArticleAll() {
        List<GetArticleResponseDto> list = articleService.getArticleList();
        return ResponseEntity.ok(list);
    }

    // 게시물 단건(articleId) 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<GetArticleResponseDto> getArticleBy(@PathVariable Long articleId) {
        GetArticleResponseDto article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    // 게시물 저장
    @PostMapping(value = "/article", consumes ={MediaType.APPLICATION_JSON_VALUE
            , MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SaveArticleResponseDto> saveArticle(@RequestPart SaveArticleRequestDto requestDto
            , @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

        Users user = userService.findBy(requestDto.getUserId());

        requestDto.setUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.saveArticle(requestDto, files));
    }

    // 게시물(articleId) 수정
    @PutMapping(value ="/article/{articleId}", consumes ={MediaType.APPLICATION_JSON_VALUE
            , MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UpdateArticleResponseDto> updateArticle(@PathVariable Long articleId
            , @RequestPart UpdateArticleRequestDto requestDto
            , @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

        return ResponseEntity.ok(articleService.updateArticle(requestDto, files, articleId));
    }

    // 게시물(articleId) 삭제
    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId
            ,  @AuthenticationPrincipal Users user) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.ok().build();
    }

    // 유저(userId)가 작성한 게시물 조회
    @GetMapping("/article/user/{userId}")
    public ResponseEntity<List<GetArticleResponseDto>> getArticlesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(articleService.getArticlesByUserId(userId));
    }

    // 게시물(articleId)과 해당 게시물의 댓글 함께 조회
    @GetMapping("/article/comment/{articleId}")
    public ResponseEntity<GetArticleWithCommentResponseDto> getArticleWithComment(@PathVariable Long articleId) {
        GetArticleWithCommentResponseDto dtoList = articleService.getArticleWithCommentByArticleId(articleId);
        return ResponseEntity.ok(dtoList);
    }


}
