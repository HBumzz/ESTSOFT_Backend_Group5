package com.app.salty.board.service;

import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.LikeArticle;
import com.app.salty.board.entity.LikeComment;
import com.app.salty.board.repository.LikeArticleRepository;
import com.app.salty.board.repository.LikeCommentRepository;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    LikeArticleRepository likeArticleRepository;
    LikeCommentRepository likeCommentRepository;

    LikeServiceImpl(LikeArticleRepository articleRepository
            , LikeCommentRepository commentRepository) {

        this.likeArticleRepository= articleRepository;
        this.likeCommentRepository=commentRepository;
    }

    @Override
    public void Like(LikeRequestDto dto) {

        if (dto.getContentType().equals(ContentType.ARTICLE)) {
            LikeArticle like = likeArticleRepository
                    .findByArticleAndUserId(dto.getArticle(),dto.getUser_id()).orElse(null);
            if(like ==null) {
                likeArticleRepository.save(dto.convertLikeArticleEntity());
            } else {
                likeArticleRepository.delete(like);
            }
        } else if(dto.getContentType().equals(ContentType.COMMENT)) {
            LikeComment like = likeCommentRepository
                    .findByCommentAndUserId(dto.getComment(),dto.getUser_id()).orElse(null);
            if(like == null) {
                likeCommentRepository.save(dto.convertLikeCommentEntity());
            }else {
                likeCommentRepository.delete(like);
            }
        }
    }

    @Override
    public Integer countLike(LikeRequestDto dto) {
        if (dto.getContentType().equals(ContentType.ARTICLE)) {
            return likeArticleRepository.countByArticle(dto.getArticle());
        } else if(dto.getContentType().equals(ContentType.COMMENT)) {
            return likeCommentRepository.countByComment(dto.getComment());
        }
        return 0;
    }
}
