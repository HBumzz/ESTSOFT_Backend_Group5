package com.app.salty.board.dto.comment;

import com.app.salty.board.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.app.salty.util.DateFormatUtil.formatter;

@NoArgsConstructor
@Getter
public class SaveCommentResponseDto {
    private Long commentId;
    private Long writerId;
    private Long articleId;
    private String content;
    private String createdAt;

    public SaveCommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.writerId = comment.getUser().getId();
        this.articleId = comment.getArticle().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().format(formatter);
    }

}
