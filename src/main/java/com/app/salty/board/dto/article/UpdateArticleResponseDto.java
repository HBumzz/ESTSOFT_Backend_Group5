package com.app.salty.board.dto.article;

import com.app.salty.board.entity.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.app.salty.util.DateFormatUtil.formatter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateArticleResponseDto {
    private Long articleId;
    private Long writerId;
    private String title;
    private String updatedAt;

    public UpdateArticleResponseDto(Article article) {
        this.articleId = article.getId();
        this.writerId= article.getUser().getId();
        this.title= article.getTitle();
        this.updatedAt = article.getUpdatedAt().format(formatter);
    }
}
