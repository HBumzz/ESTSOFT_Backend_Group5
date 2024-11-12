package com.app.salty.admin.challengeBoard.dto.response;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeViewResponse {
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Challenge.ChallengeStatus status;

    private Challenge.ChallengeType type;

    public ChallengeViewResponse(Challenge challenge) {
        id = challenge.getId();
        title = challenge.getTitle();
        content = challenge.getContent();
        createdAt = challenge.getCreatedAt();
        updatedAt = challenge.getUpdatedAt();
        startDate = challenge.getStartDate();
        endDate = challenge.getEndDate();
        status = challenge.getStatus();
        type = challenge.getType();
    }
}
