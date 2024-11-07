package com.app.salty.board.repository;

import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.LikeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeArticleRepository extends JpaRepository<LikeArticle, Long> {
    Optional<LikeArticle> findByArticleAndUserId(Article article, Long userId);
    Optional<Integer> countByArticle(Article article);
}
