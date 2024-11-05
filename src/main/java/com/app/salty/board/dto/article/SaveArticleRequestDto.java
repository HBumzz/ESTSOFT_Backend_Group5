package com.app.salty.board.dto.article;

import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.ArticleHeader;
import com.app.salty.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaveArticleRequestDto {

    private Users user;
    private ArticleHeader header;
    private String title;
    private String content;

    public Article toEntity(){
        return new Article(user,header,title,content);
    }
}
