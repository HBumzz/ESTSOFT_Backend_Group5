package com.app.salty.board.service;

import com.app.salty.board.dto.article.*;
import com.app.salty.user.entity.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    List<GetArticleResponseDto> getArticleList();
    GetArticleResponseDto getArticleById(Long id);
    SaveArticleResponseDto saveArticle(SaveArticleRequestDto dto);
    UpdateArticleResponseDto updateArticle(UpdateArticleRequestDto dto);
    void deleteArticle(Long id);

    List<GetArticleResponseDto> getArticlesByUserId(Long Id);

    GetArticleWithCommentResponseDto getArticleWithCommentByArticleId(Long Id, CustomUserDetails user);
}
