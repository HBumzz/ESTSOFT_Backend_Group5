package com.app.salty.admin.challengeBoard.repository;

import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeComment, Long> {
}
