package com.app.salty.board.dto.article;

import com.app.salty.board.entity.ArticleHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateArticleRequestDto {
    private ArticleHeader header;
    private String title;
    private String content;
}
