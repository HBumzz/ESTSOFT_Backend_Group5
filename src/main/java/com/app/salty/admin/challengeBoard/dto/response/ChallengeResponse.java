package com.app.salty.admin.challengeBoard.dto.response;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Challenge.ChallengeStatus status;

    public ChallengeResponse(Challenge challenge) {
        id = challenge.getId();
        title = challenge.getTitle();
        content = challenge.getContent();
        createdAt = challenge.getCreatedAt();
        updatedAt = challenge.getUpdatedAt();
        startDate = challenge.getStartDate();
        endDate = challenge.getEndDate();
        status = challenge.getStatus();
    }

    public ChallengeResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

}