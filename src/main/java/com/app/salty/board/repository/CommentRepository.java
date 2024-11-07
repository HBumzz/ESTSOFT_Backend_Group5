package com.app.salty.board.repository;

import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findCommentsByArticle_Id(Long article_id);
    List<Comment> findCommentsByUser_Id(Long user_id);
}
