package com.app.salty.board.dto.comment;

import com.app.salty.board.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.app.salty.util.DateFormatUtil.formatter;

@NoArgsConstructor
@Getter
@Setter
public class GetCommentResponseDto {
    private Long commentId;
    private Long writerId;
    private String writerNickname;
    private String writerName;
    private String articleType;
    private Long articleId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Integer likeCount;

    public GetCommentResponseDto(Comment comment) {
        this.commentId=comment.getCommentId();
        this.writerId = comment.getUserId();
        this.articleType =comment.getType().name();
        this.articleId = comment.getArticle().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().format(formatter);
        this.updatedAt = comment.getUpdatedAt().format(formatter);
    }
}
