package com.app.salty.admin.challengeBoard.dto.request;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChallengeRequest {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Challenge.ChallengeStatus status;
    private Challenge.ChallengeType type;
}