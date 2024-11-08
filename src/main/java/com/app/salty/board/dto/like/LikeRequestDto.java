package com.app.salty.board.dto.like;

import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.entity.LikeArticle;
import com.app.salty.board.entity.LikeComment;
import com.app.salty.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LikeRequestDto {
    private ContentType contentType;
    private Long contentId;
    private Article article;
    private Comment comment;
    private Long user_id;

    public LikeArticle convertLikeArticleEntity() {
        return new LikeArticle(article,user_id);
    }

    public LikeComment convertLikeCommentEntity() {
        return new LikeComment(comment,user_id);
    }
}
