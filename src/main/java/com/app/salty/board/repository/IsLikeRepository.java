package com.app.salty.board.repository;

import com.app.salty.board.entity.IsLike;
import com.app.salty.board.entity.LikeContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsLikeRepository extends JpaRepository<IsLike,Long> {
    Integer countIsLikesByContentTypeAndContentId(LikeContentType contentType, Long contentId);
}
