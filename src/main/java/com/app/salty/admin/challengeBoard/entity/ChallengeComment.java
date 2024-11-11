package com.app.salty.admin.challengeBoard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor
public class ChallengeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String body;  // 댓글 내용

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 수정일

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;  // 어떤 챌린지에 대한 댓글인지

    // 생성자
    public ChallengeComment(String body, Challenge challenge) {
        this.body = body;
        this.challenge = challenge;
    }

    // 댓글 수정 메서드
    public void updateCommentBody(String body) {
        this.body = body;
    }
}
