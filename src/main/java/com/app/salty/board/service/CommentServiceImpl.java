package com.app.salty.board.service;

import com.app.salty.board.dto.comment.*;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    ArticleRepository articleRepository;

    CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public List<GetCommentResponseDto> getCommentList() {
        List<Comment> list = commentRepository.findAll();
        return list.stream().map(GetCommentResponseDto::new).toList();
    }

    @Override
    public GetCommentResponseDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return new GetCommentResponseDto(comment);
    }

    @Override
    public SaveCommentResponseDto saveComment(SaveCommentRequestDto dto, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        dto.setArticle(article);
        Comment comment = commentRepository.save(dto.toEntity());
        return new SaveCommentResponseDto(comment);
    }

    @Override
    public UpdateCommentResponseDto updateComment(UpdateCommentRequestDto dto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        comment.setContent(dto.getContent());
        return new UpdateCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<GetCommentResponseDto> getCommentsByArticleId(Long articleId) {
        List<Comment> commentList = commentRepository.findCommentsByArticle_Id(articleId);
        return commentList.stream().map(GetCommentResponseDto::new).toList();
    }

    @Override
    public List<GetCommentResponseDto> getCommentsByUserId(Long userId) {
        List<Comment> commentList = commentRepository.findCommentsByUser_Id(userId);
        return commentList.stream().map(GetCommentResponseDto::new).toList();
    }
}
