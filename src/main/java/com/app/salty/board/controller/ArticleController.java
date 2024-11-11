package com.app.salty.board.controller;

import com.app.salty.board.dto.article.*;
import com.app.salty.board.entity.ArticleHeader;
import com.app.salty.board.service.ArticleServiceImpl;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class ArticleController {
    ArticleServiceImpl articleService;
    UserService userService;

    ArticleController(ArticleServiceImpl articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }
// ====================================================================================================================

    private final String uploadDir = Paths.get("C:", "tui-editor", "upload").toString();

    /**
     * 에디터 이미지 업로드
     * @param image 파일 객체
     * @return 업로드된 파일명
     */
    @PostMapping("/image-upload")
    public String uploadEditorImage(@RequestParam final MultipartFile image) {
        if (image.isEmpty()) {
            return "";
        }

        String orgFilename = image.getOriginalFilename();                                         // 원본 파일명
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");           // 32자리 랜덤 문자열
        assert orgFilename != null;
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);  // 확장자
        String saveFilename = uuid + "." + extension;                                             // 디스크에 저장할 파일명
        String fileFullPath = Paths.get(uploadDir, saveFilename).toString();                      // 디스크에 저장할 파일의 전체 경로

        // uploadDir에 해당되는 디렉터리가 없으면, uploadDir에 포함되는 전체 디렉터리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 파일 저장 (write to disk)
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            return saveFilename;

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }

    /**
     * 디스크에 업로드된 파일을 byte[]로 반환
     * @param filename 디스크에 업로드된 파일명
     * @return image byte array
     */
    @GetMapping(value = "/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] printEditorImage(@RequestParam final String filename) {
        // 업로드된 파일의 전체 경로
        String fileFullPath = Paths.get(uploadDir, filename).toString();

        // 파일이 없는 경우 예외 throw
        File uploadedFile = new File(fileFullPath);
        if (!uploadedFile.exists()) {
            throw new RuntimeException();
        }

        try {
            // 이미지 파일을 byte[]로 변환 후 반환
            return Files.readAllBytes(uploadedFile.toPath());

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }

    //====================================================================================================================



    // 게시물 전체 조회
    @GetMapping("/article")
    public ResponseEntity<List<GetArticleResponseDto>> getArticleAll(@AuthenticationPrincipal UserDetails user) {
        log.info(user.toString());
        List<GetArticleResponseDto> list = articleService.getArticleList();
        return ResponseEntity.ok(list);
    }

    // 게시물 단건(articleId) 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<GetArticleResponseDto> getArticleBy(@PathVariable Long articleId) {
        GetArticleResponseDto article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    // 게시물 저장
    @PostMapping(value = "/article")
    public ResponseEntity<SaveArticleResponseDto> saveArticle(@RequestBody SaveArticleRequestDto requestDto) {
        // 임의의 유저로 정보 전달
//        Users user = userService.findBy(1L);
//        requestDto.setUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.saveArticle(requestDto));
    }

    // 게시물(articleId) 수정
    @PutMapping(value ="/article/{articleId}")
    public ResponseEntity<UpdateArticleResponseDto> updateArticle(@RequestBody UpdateArticleRequestDto requestDto
            , @PathVariable Long articleId) {
        requestDto.setUserId(1L);
        return ResponseEntity.ok(articleService.updateArticle(requestDto));
    }

    // 게시물(articleId) 삭제
    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId
            ,  @AuthenticationPrincipal Users user) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.ok().build();
    }

    // 유저(userId)가 작성한 게시물 조회
    @GetMapping("/article/user/{userId}")
    public ResponseEntity<List<GetArticleResponseDto>> getArticlesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(articleService.getArticlesByUserId(userId));
    }

    // 게시물(articleId)과 해당 게시물의 댓글 함께 조회
    @GetMapping("/article/comment/{articleId}")
    public ResponseEntity<GetArticleWithCommentResponseDto> getArticleWithComment(@PathVariable Long articleId) {
        GetArticleWithCommentResponseDto dtoList = articleService.getArticleWithCommentByArticleId(articleId);
        return ResponseEntity.ok(dtoList);
    }


}
