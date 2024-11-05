package com.app.salty.board.controller;

import com.app.salty.board.dto.islike.IsLikeRequestDto;
import com.app.salty.board.dto.islike.IsLikeResponseDto;
import com.app.salty.board.service.IsLikeServiceImpl;
import com.app.salty.user.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IsLikeController {

    IsLikeServiceImpl isLikeService;

    IsLikeController(IsLikeServiceImpl isLikeService, IsLikeServiceImpl isLikeServiceImpl) {
        this.isLikeService = isLikeService;
    }

    @PostMapping("/like/article/{articleId}")
    public ResponseEntity<IsLikeResponseDto> likeArticle(@RequestBody IsLikeRequestDto requestDto
            , @PathVariable Long articleId
            , @AuthenticationPrincipal Users user) {

        // 임의의 유저 생성 - test
        Users tempUser = new Users();
        tempUser.setId(1L);
        // ==============================

        requestDto.setUser(tempUser);
        IsLikeResponseDto responseDto = isLikeService.likeArticle(requestDto,articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/like/comment/{commentId}")
    public ResponseEntity<IsLikeResponseDto> likeComment(@RequestBody IsLikeRequestDto requestDto
            , @PathVariable Long commentId
            , @AuthenticationPrincipal Users user) {

        // 임의의 유저 생성 - test
        Users tempUser = new Users();
        tempUser.setId(1L);
        // ==============================

        requestDto.setUser(tempUser);
        IsLikeResponseDto responseDto = isLikeService.likeComment(requestDto,commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/like/countArticle/{articleId}")
    public ResponseEntity<Integer> likeCountArticle(@PathVariable Long articleId) {
        Integer count = isLikeService.countLikesArticle(articleId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/like/countComment/{commentId}")
    public ResponseEntity<Integer> likeCountComment(@PathVariable Long commentId) {
        Integer count = isLikeService.countLikesComment(commentId);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/like/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long likeId) {
        isLikeService.deleteLike(likeId);
        return ResponseEntity.ok().build();
    }
}
