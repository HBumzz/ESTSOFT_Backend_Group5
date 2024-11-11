package com.app.salty.board.service;

import com.app.salty.board.dto.article.*;
import com.app.salty.board.dto.comment.GetCommentResponseDto;
import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    ArticleRepository articleRepository;
    CommentRepository commentRepository;
    LikeServiceImpl likeService;
    UserRepository userRepository;

    ArticleServiceImpl(ArticleRepository articleRepository
            , CommentRepository commentRepository
            , LikeServiceImpl likeService, UserRepository userRepository) {

        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.likeService=likeService;
        this.userRepository=userRepository;
    }

    @Override
    public List<GetArticleResponseDto> getArticleList() {
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();

        return articleList.stream().map(GetArticleResponseDto::new).toList();
    }

    @Override
    public GetArticleResponseDto getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return new GetArticleResponseDto(article);
    }

    @Override
    public SaveArticleResponseDto saveArticle(SaveArticleRequestDto dto) {
        Users user = userRepository.findById(dto.getUserId()).orElseThrow(IllegalArgumentException::new);
        dto.setUser(user);
        Article article = articleRepository.save(dto.toEntity());
        return new SaveArticleResponseDto(article);
    }

    @Transactional
    @Override
    public UpdateArticleResponseDto updateArticle(UpdateArticleRequestDto dto){

        Article article = articleRepository.findById(dto.getArticleId()).orElseThrow(IllegalArgumentException::new);

        article.setHeader(dto.getHeader());
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());

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
    public GetArticleWithCommentResponseDto getArticleWithCommentByArticleId(Long articleId, CustomUserDetails user) {
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
            getCommentResponseDto.setWriterNickname(user.getNickname());
            getCommentResponseDto.setWriterName(user.getUsername());
        }
        return new GetArticleWithCommentResponseDto(article,commentResponseDtoList);
    }
}
