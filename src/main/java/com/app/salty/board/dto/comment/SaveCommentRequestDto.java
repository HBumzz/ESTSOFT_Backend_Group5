package com.app.salty.board.dto.comment;

import com.app.salty.board.entity.ArticleType;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SaveCommentRequestDto {
    private ArticleType type;
    private Long userId;
    private Article article;
    private String content;

    public Comment toEntity() {
        return new Comment(type, userId,article,content);
    }
}
