package com.app.salty.board.service;

import com.app.salty.board.dto.comment.*;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import com.app.salty.util.PointService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    PointService pointService;
    CommentRepository commentRepository;
    ArticleRepository articleRepository;
    UserRepository userRepository;

    CommentServiceImpl(CommentRepository commentRepository
            , ArticleRepository articleRepository, UserRepository userRepository, PointService pointService) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.pointService = pointService;
    }

    @Override
    public List<GetCommentResponseDto> getCommentList() {
        List<Comment> list = commentRepository.findAll();
        return list.stream().map(GetCommentResponseDto::new).toList();
    }

    @Override
    public GetCommentResponseDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);

        return new GetCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public SaveCommentResponseDto saveComment(SaveCommentRequestDto dto, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        dto.setArticle(article);
        Comment comment = commentRepository.save(dto.toEntity());
        Users user = userRepository.findById(comment.getUserId()).orElseThrow(IllegalArgumentException::new);
        pointService.addPoint(user,100L);
        return new SaveCommentResponseDto(comment);
    }

    @Override
    public UpdateCommentResponseDto updateComment(UpdateCommentRequestDto dto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        comment.setContent(dto.getContent());
        return new UpdateCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<GetCommentResponseDto> getCommentsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        List<Comment> commentList = commentRepository.findCommentsByArticle(article);
        return commentList.stream().map(GetCommentResponseDto::new).toList();
    }

    @Override
    public List<GetCommentResponseDto> getCommentsByUserId(Long userId) {
        List<Comment> commentList = commentRepository.findCommentsByUserId(userId);
        return commentList.stream().map(GetCommentResponseDto::new).toList();
    }

    @Override
    public Integer countCommentByArticle(Article article) {
        return commentRepository.countCommentsByArticle(article).orElse(0);
    }
}
