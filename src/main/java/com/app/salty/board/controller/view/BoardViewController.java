package com.app.salty.board.controller.view;

import com.app.salty.board.dto.MessageDto;
import com.app.salty.board.dto.article.GetArticleResponseDto;
import com.app.salty.board.dto.article.GetArticleWithCommentResponseDto;
import com.app.salty.board.dto.comment.SaveCommentRequestDto;
import com.app.salty.board.dto.comment.SaveCommentResponseDto;
import com.app.salty.board.dto.like.ContentType;
import com.app.salty.board.dto.like.LikeRequestDto;
import com.app.salty.board.entity.Article;
import com.app.salty.board.entity.Comment;
import com.app.salty.board.repository.ArticleRepository;
import com.app.salty.board.repository.CommentRepository;
import com.app.salty.board.service.ArticleServiceImpl;
import com.app.salty.board.service.CommentServiceImpl;
import com.app.salty.board.service.LikeServiceImpl;
import com.app.salty.user.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class BoardViewController {

    @Autowired
    ArticleServiceImpl articleService;
    @Autowired
    CommentServiceImpl commentService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikeServiceImpl likeService;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("data", params);
        return "common/message";
    }


    @GetMapping("/board")
    public String board(Model model) {
        List<GetArticleResponseDto> dtoList = articleService.getArticleList();
        for (GetArticleResponseDto responseDto : dtoList) {
            Article article = articleRepository.findById(responseDto.getArticleId()).orElseThrow(IllegalArgumentException::new);
            Integer count = commentService.countCommentByArticle(article);
            responseDto.setCommentCount(count);
        }
        model.addAttribute("articles", dtoList);
        return "board/boardList";
    }

    @GetMapping("/board/article/{articleId}")
    public String showArticle(@PathVariable Long articleId, Model model) {
        GetArticleWithCommentResponseDto responseDto = articleService.getArticleWithCommentByArticleId(articleId);
        Article article = articleRepository.findById(responseDto.getArticleId()).orElseThrow(IllegalArgumentException::new);

        LikeRequestDto likeDto = new LikeRequestDto();
        likeDto.setContentType(ContentType.ARTICLE);
        likeDto.setArticle(article);
        Integer countLike = likeService.countLike(likeDto);

        responseDto.setLikeCount(countLike);

        Integer countComment = commentService.countCommentByArticle(article);
        responseDto.setCommentCount(countComment);

        model.addAttribute("article", responseDto);

        return "board/showArticle";
    }

    @GetMapping("/comment/delete/{articleId}/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @PathVariable Long articleId, Model model) {
        commentService.deleteComment(commentId);
        String aid = String.valueOf(articleId);
        String href = "/board/article/" + aid;
        log.warn(href);
        MessageDto message = new MessageDto("댓글 삭제 완료!", href);
        return showMessageAndRedirect(message, model);
    }

    // 게시판에 댓글 달기
    @PostMapping("/comment")
    public String saveComment(
            SaveCommentRequestDto requestDto
            , Long articleId
            , @AuthenticationPrincipal Users user, Model model) {

        // 임의의 유저 생성 - test
        Users tempUser = new Users();
        tempUser.setId(1L);
        // ==============================
        requestDto.setUser(tempUser);

        SaveCommentResponseDto responseDto = commentService.saveComment(requestDto, articleId);

        String href = "/board/article/" + articleId;
        MessageDto message = new MessageDto("댓글 작성 완료!", href);
        return showMessageAndRedirect(message, model);
    }

    // 댓글 좋아요
    @GetMapping("/comment/like/{articleId}/{commentId}")
    public String likeComment(@PathVariable Long commentId, @PathVariable Long articleId
            , @AuthenticationPrincipal Users user, Model model) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        requestDto.setContentId(commentId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);

        likeService.Like(requestDto);

        return "redirect:/board/article/" + articleId;
    }

    // 새글 작성 페이지
    @GetMapping("/board/new")
    public String newArticle() {
        return "board/newArticle";
    }
}
