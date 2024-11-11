package com.app.salty.board.service;

import com.app.salty.board.dto.ImagesDto.ImagesResponseDto;
import com.app.salty.board.dto.article.*;
import com.app.salty.board.dto.comment.GetCommentResponseDto;
import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.entity.Image;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.board.repository.ImagesRepository;
import com.app.salty.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;
    ArticleRepository articleRepository;
    CommentRepository commentRepository;
    ImagesRepository imagesRepository;
    LikeServiceImpl likeService;

    ArticleServiceImpl(ArticleRepository articleRepository
            , CommentRepository commentRepository
            , ImagesRepository imagesRepository, UserService userService
            , LikeServiceImpl likeService) {

        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.imagesRepository = imagesRepository;
        this.userService = userService;
        this.likeService=likeService;
    }

    @Override
    public List<GetArticleResponseDto> getArticleList() {
        List<Article> articleList = articleRepository.findAll();

        return articleList.stream().map(GetArticleResponseDto::new).toList();
    }

    @Override
    public GetArticleResponseDto getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        List<Image> imageList = imagesRepository.findImagesByArticle_Id(article.getId());
        List<ImagesResponseDto> imagesResponseDtoList = imageList.stream().map(ImagesResponseDto::new).toList();
        GetArticleResponseDto responseDto = new GetArticleResponseDto(article);
        responseDto.setImageList(imagesResponseDtoList);
        return responseDto;
    }

    @Override
    public SaveArticleResponseDto saveArticle(SaveArticleRequestDto dto) {
        Article article = articleRepository.save(dto.toEntity());
        return new SaveArticleResponseDto(article);
    }

    @Transactional
    @Override
    public UpdateArticleResponseDto updateArticle(UpdateArticleRequestDto dto){

        userService.findBy(dto.getUserId()); // 사용자 인증

        Article article = articleRepository.findById(dto.getArticleId()).orElseThrow(IllegalArgumentException::new);
        if(!Objects.equals(article.getUser().getId(), dto.getUserId())) {
            throw new IllegalArgumentException("로그인 정보와 작성자 정보가 다릅니다.");
        }

        article.setHeader(dto.getHeader());
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());

        imagesRepository.deleteImagesByArticle_Id(article.getId());

        Article newArticle = articleRepository.save(article);
        return new UpdateArticleResponseDto(newArticle);
    }

    @Transactional
    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        List<Comment> commentList = commentRepository.findCommentsByArticle(article);
        for (Comment comment : commentList) {
            commentRepository.deleteById(comment.getCommentId());
        }

        articleRepository.deleteById(id);
    }

    @Override
    public List<GetArticleResponseDto> getArticlesByUserId(Long userId) {
        List<Article> list = articleRepository.findArticlesByUserId(userId);
        return list.stream().map(GetArticleResponseDto::new).toList();
    }

    @Override
    public GetArticleWithCommentResponseDto getArticleWithCommentByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        List<Comment> commentList = commentRepository.findCommentsByArticle(article);
        List<GetCommentResponseDto> commentResponseDtoList = commentList.stream().map(GetCommentResponseDto::new).toList();
        for (GetCommentResponseDto getCommentResponseDto : commentResponseDtoList) {
            Long commentId = getCommentResponseDto.getCommentId();
            LikeRequestDto requestDto = new LikeRequestDto();
            requestDto.setContentType(ContentType.COMMENT);
            Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
            requestDto.setComment(comment);
            Integer count = likeService.countLike(requestDto);
            getCommentResponseDto.setLikeCount(count);
        }
        return new GetArticleWithCommentResponseDto(article,commentResponseDtoList);
    }
}
