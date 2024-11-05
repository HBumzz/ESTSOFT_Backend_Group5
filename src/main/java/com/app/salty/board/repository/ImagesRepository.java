package com.app.salty.board.repository;

import com.app.salty.board.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images,Long> {
    List<Images> findImagesByArticle_Id(Long article_id);
}
