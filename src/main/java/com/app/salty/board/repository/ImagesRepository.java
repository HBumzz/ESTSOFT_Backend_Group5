package com.app.salty.board.repository;

import com.app.salty.board.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Image,Long> {
    List<Image> findImagesByArticle_Id(Long article_id);
}
