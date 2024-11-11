package com.app.salty.board.service;

import com.app.salty.board.dto.like.LikeRequestDto;

public interface LikeService {
    void Like(LikeRequestDto dto);
    Integer countLike(LikeRequestDto dto);
}
