package com.app.salty.admin.challengeBoard.dto.response;

import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private Long challengeId;
    private String body;
    private String createdAt;
    private ChallengeResponse challenge;

    // 날짜 포맷 지정
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CommentResponseDTO(ChallengeComment comment) {
        Challenge challengeFromComment = comment.getChallenge();
        this.commentId = comment.getId();
        this.challengeId = challengeFromComment.getId();
        this.body = comment.getBody();
        this.createdAt = comment.getCreatedAt().format(formatter);  // LocalDateTime을 String으로 포맷
        this.challenge = new ChallengeResponse(challengeFromComment);  // ChallengeResponse 생성
    }
}
