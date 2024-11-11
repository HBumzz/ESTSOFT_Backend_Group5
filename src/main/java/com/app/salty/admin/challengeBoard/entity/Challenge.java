package com.app.salty.admin.challengeBoard.entity;

import com.app.salty.admin.challengeBoard.dto.response.ChallengeResponse;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public enum ChallengeStatus {
        PENDING,
        ONGOING,
        COMPLETED
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    public enum ChallengeType {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ChallengeType type;


    // 생성자
    @Builder
    public Challenge(String title, String content, LocalDateTime startDate, LocalDateTime endDate, ChallengeStatus status) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public ChallengeResponse convert() {
        return new ChallengeResponse(this);
    }

    public void update(String title, String content, LocalDateTime startDate, LocalDateTime endDate, ChallengeStatus status, ChallengeType type) {
//        if (!title.isBlank()) { this.title = title; }
//        if (!content.isBlank()) { this.content = content; }
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.type = type;
    }
}
