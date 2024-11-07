package com.app.salty.board.dto.islike;

import com.app.salty.board.entity.IsLike;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IsLikeResponseDto {
    private Long user_id;
    private String contentType;
    private Long contentId;

    public IsLikeResponseDto(IsLike like) {
        this.user_id = like.getUser().getId();
        this.contentType = like.getContentType().name();
        this.contentId = like.getContentId();
    }
}
