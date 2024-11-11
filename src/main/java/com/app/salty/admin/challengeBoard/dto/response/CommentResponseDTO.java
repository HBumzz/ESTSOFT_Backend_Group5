package com.app.salty.admin.challengeBoard.dto.response;

import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.app.salty.util.DateFormatUtil.formatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private Long challengeId;
    private String body;
    private String createdAt;
    private ChallengeResponse challenge;
    public CommentResponseDTO(ChallengeComment comment) {
        Challenge challengeFromComment = comment.getChallenge();
        commentId = comment.getId();
        challengeId = challengeFromComment.getId();
        body = comment.getBody();
        createdAt = comment.getCreatedAt().format(formatter);
        challenge = new ChallengeResponse(challengeFromComment);
    }
}
