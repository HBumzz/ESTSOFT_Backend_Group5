package com.app.salty.admin.challengeBoard.dto.request;

import com.app.salty.admin.challengeBoard.dto.response.ChallengeResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentRequestDTO {
    private Long commentId;
    private Long challengeId;
    private String body;
    private String createdAt;
    private ChallengeResponse challenge;
}
