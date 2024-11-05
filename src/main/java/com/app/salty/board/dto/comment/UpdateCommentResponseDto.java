package com.app.salty.board.dto.comment;

import com.app.salty.board.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.app.salty.util.DateFormatUtil.formatter;

@NoArgsConstructor
@Getter
public class UpdateCommentResponseDto {
    private Long commentId;
    private Long writerId;
    private String articleType;
    private Long articleId;
    private String content;
    private String createdAt;
    private String updatedAt;

    public UpdateCommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.writerId = comment.getUser().getId();
        this.articleType = comment.getType().getName();
        this.articleId = comment.getArticle().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().format(formatter);
        this.updatedAt = comment.getUpdatedAt().format(formatter);
    }
}
