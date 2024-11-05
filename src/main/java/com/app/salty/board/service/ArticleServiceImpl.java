package com.app.salty.board.service;

import com.app.salty.board.dto.ImagesDto.ImagesResponseDto;
import com.app.salty.board.dto.article.*;
import com.app.salty.board.dto.comment.GetCommentResponseDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.entity.Images;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.board.repository.ImagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    ArticleRepository articleRepository;
    CommentRepository commentRepository;
    ImagesRepository imagesRepository;

    private final String fileDir = "C:\\Users\\leejinhun\\Downloads\\server\\";

    ArticleServiceImpl(ArticleRepository articleRepository
            , CommentRepository commentRepository
            , ImagesRepository imagesRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.imagesRepository = imagesRepository;
    }

    @Override
    public List<GetArticleResponseDto> getArticleList() {
        List<Article> list = articleRepository.findAll();
        return list.stream().map(GetArticleResponseDto::new).toList();
    }

    @Override
    public GetArticleResponseDto getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        List<Images> imagesList = imagesRepository.findImagesByArticle_Id(article.getId());
        List<ImagesResponseDto> imagesResponseDtoList = imagesList.stream().map(ImagesResponseDto::new).toList();
        GetArticleResponseDto responseDto = new GetArticleResponseDto(article);
        responseDto.setImageList(imagesResponseDtoList);
        return responseDto;
    }

    @Transactional
    @Override
    public SaveArticleResponseDto saveArticle(SaveArticleRequestDto dto, MultipartFile[] multipartFiles) throws IOException {
        Article article = articleRepository.save(dto.toEntity());

        log.info("multipartFile = {}", multipartFiles);
        if(!Objects.isNull(multipartFiles)) {
            for(MultipartFile file : multipartFiles) {
                String originalFileName = file.getOriginalFilename();
                log.info("originalFilename ={}",originalFileName);

                long size = file.getSize();
                log.info("size ={}",size);

                String contentType = file.getContentType();
                log.info("contentType={}", contentType);

                String filePath = fileDir + originalFileName;
                log.info("filePath = {}" , filePath);

                Images image = new Images(originalFileName,originalFileName,filePath,size,contentType,article);
                imagesRepository.save(image);

                file.transferTo(new File(filePath));
            }
        }

        return new SaveArticleResponseDto(article);
    }

    @Override
    public UpdateArticleResponseDto updateArticle(UpdateArticleRequestDto dto, Long articleId)  {

        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        article.setHeader(dto.getHeader());
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());

        Article newArticle = articleRepository.save(article);
        return new UpdateArticleResponseDto(newArticle);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        articleRepository.deleteById(id);
    }

    @Override
    public List<GetArticleResponseDto> getArticlesByUserId(Long userId) {
        List<Article> list = articleRepository.findArticlesByUserId(userId);
        return list.stream().map(GetArticleResponseDto::new).toList();
    }

    @Override
    public GetArticleWithCommentResponseDto getArticleWithCommentByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        List<Comment> commentList = commentRepository.findCommentsByArticle_Id(articleId);
        List<GetCommentResponseDto> commentResponseDtoList = commentList.stream().map(GetCommentResponseDto::new).toList();
        return new GetArticleWithCommentResponseDto(article,commentResponseDtoList);
    }
}
