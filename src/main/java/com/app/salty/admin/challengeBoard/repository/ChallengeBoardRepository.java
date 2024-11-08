package com.app.salty.admin.challengeBoard.repository;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeBoardRepository extends JpaRepository<Challenge, Long> {
}
