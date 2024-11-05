package com.app.salty.board.dto.article;

import com.app.salty.board.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.app.salty.util.DateFormatUtil.formatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveArticleResponseDto {
    private Long articleId;
    private Long writerId;
    private String title;
    private String createdAt;

    public SaveArticleResponseDto(Article article) {
        this.articleId = article.getId();
        this.writerId= article.getUser().getId();
        this.title= article.getTitle();
        this.createdAt = article.getCreatedAt().format(formatter);
    }
}
