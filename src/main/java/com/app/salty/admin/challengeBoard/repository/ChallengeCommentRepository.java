package com.app.salty.admin.challengeBoard.repository;

import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeComment, Long> {
    public List<ChallengeComment> findByChallengeId(Long id);
}