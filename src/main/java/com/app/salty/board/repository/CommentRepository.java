package com.app.salty.board.repository;

import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.user.entity.Users;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findCommentsByArticle(Article article);
    List<Comment> findCommentsByUserId(Long userId);
    Optional<Integer> countCommentsByArticle(Article article);
}
