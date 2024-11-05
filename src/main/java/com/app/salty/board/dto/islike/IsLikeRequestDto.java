package com.app.salty.board.dto.islike;

import com.app.salty.board.entity.IsLike;
import com.app.salty.board.entity.LikeContentType;
import com.app.salty.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IsLikeRequestDto {
    private Users user;
    private LikeContentType contentType;
    private Long contentId;

    public IsLike toEntity() {
        return new IsLike(user,contentType,contentId);
    }
}
