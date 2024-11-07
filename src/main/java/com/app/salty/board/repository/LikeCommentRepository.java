package com.app.salty.board.repository;

import com.app.salty.board.entity.Comment;
import com.app.salty.board.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
    Optional<LikeComment> findByCommentAndUserId(Comment comment, Long userId);
    Optional<Integer> countByComment(Comment comment);
}
