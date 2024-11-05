package com.app.salty.admin.challengeBoard.dto.request;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddChallengeRequest {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Challenge.ChallengeStatus status;

    public Challenge toEntity() {
        return Challenge.builder()
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status)
                .build();
    }
}