package com.app.salty.board.dto.article;

import com.app.salty.board.dto.comment.GetCommentResponseDto;
import com.app.salty.board.entity.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.app.salty.util.DateFormatUtil.formatter;

@NoArgsConstructor
@Getter
@Setter
public class GetArticleWithCommentResponseDto {
    private Long articleId;
    private Long writerId;
    private String writerNickname;
    private String writerName;
    private String header;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Integer likeCount;
    private Integer commentCount;
    private List<GetCommentResponseDto> commentList;


    public GetArticleWithCommentResponseDto(Article article, List<GetCommentResponseDto> commentList) {
        this.articleId =article.getId();
        this.writerId = article.getUser().getId();
        this.writerNickname = article.getUser().getNickname();
        this.writerName = article.getUser().getEmail();
        this.header = article.getHeader().getName();
        this.title = article.getTitle();
        this.content= article.getContent();
        this.createdAt =article.getCreatedAt().format(formatter);
        this.updatedAt =article.getUpdatedAt().format(formatter);
        this.commentList = commentList;
    }
}
