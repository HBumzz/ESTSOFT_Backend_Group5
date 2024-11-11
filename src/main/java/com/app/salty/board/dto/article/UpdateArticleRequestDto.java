package com.app.salty.board.dto.article;

import com.app.salty.board.entity.ArticleHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UpdateArticleRequestDto {
    private Long userId;
    private Long articleId;
    private ArticleHeader header;
    private String title;
    private String content;
}
