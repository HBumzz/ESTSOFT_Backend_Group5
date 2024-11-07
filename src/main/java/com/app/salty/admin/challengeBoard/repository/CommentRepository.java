package com.app.salty.admin.challengeBoard.repository;

import com.app.salty.admin.challengeBoard.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
