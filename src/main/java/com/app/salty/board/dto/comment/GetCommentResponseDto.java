package com.app.salty.board.dto.comment;

import com.app.salty.board.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.app.salty.util.DateFormatUtil.formatter;

@NoArgsConstructor
@Getter
public class GetCommentResponseDto {
    private Long commentId;
    private Long userId;
    private Long articleId;
    private String content;
    private String createdAt;
    private String updatedAt;

    public GetCommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.userId = comment.getUser().getId();
        this.articleId = comment.getArticle().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().format(formatter);
        this.updatedAt = comment.getUpdatedAt().format(formatter);
    }
}
