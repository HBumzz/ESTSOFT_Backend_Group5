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
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class BoardViewController {

    @Autowired
    ArticleServiceImpl articleService;
    @Autowired
    CommentServiceImpl commentService;
    @Autowired
    UserRepository userRepository;
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


    // 게시판 보기
    @GetMapping("board")
    public String board(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<GetArticleResponseDto> dtoList = articleService.getArticleList();

        log.info(currentUser.getAuthorities().toString());
        for (GetArticleResponseDto responseDto : dtoList) {
            Article article = articleRepository.findById(responseDto.getArticleId()).orElseThrow(IllegalArgumentException::new);
            Integer count = commentService.countCommentByArticle(article);
            responseDto.setCommentCount(count);
        }

        model.addAttribute("userAuth", currentUser);
        model.addAttribute("articles", dtoList);
        return "board/boardList";
    }

    // 게시글 상세 조회
    @GetMapping("board/article/{articleId}")
    public String showArticle(@PathVariable Long articleId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {

        GetArticleWithCommentResponseDto responseDto = articleService.getArticleWithCommentByArticleId(articleId, currentUser);

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

    // 댓글 삭제
    @GetMapping("comment/delete/{articleId}/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @PathVariable Long articleId, Model model
            , @AuthenticationPrincipal CustomUserDetails currentUser) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        Long userId = comment.getUserId();

        if (!currentUser.getId().equals(userId)) {
            MessageDto message = new MessageDto("작성자만 삭제할 수 있습니다.", "/board/article/" + articleId);
            return showMessageAndRedirect(message, model);
        }


        commentService.deleteComment(commentId);
        String aid = String.valueOf(articleId);
        String href = "board/article/" + aid;
        MessageDto message = new MessageDto("댓글 삭제 완료!", href);
        return showMessageAndRedirect(message, model);
    }

    // 게시판에 댓글 달기
    @PostMapping("comment")
    public String saveComment(
            SaveCommentRequestDto requestDto
            , Long articleId
            , @AuthenticationPrincipal CustomUserDetails user
            , Model model) {

        requestDto.setUserId(user.getId());
        SaveCommentResponseDto responseDto = commentService.saveComment(requestDto, articleId);

        String href = "board/article/" + articleId;
        MessageDto message = new MessageDto("댓글 작성 완료!", href);
        return showMessageAndRedirect(message, model);
    }

    // 댓글 좋아요
    @GetMapping("comment/like/{articleId}/{commentId}")
    public String likeComment(@PathVariable Long commentId, @PathVariable Long articleId
            , @AuthenticationPrincipal CustomUserDetails user, Model model) {
        LikeRequestDto requestDto = new LikeRequestDto();
        requestDto.setContentType(ContentType.COMMENT);
        requestDto.setContentId(commentId);
        requestDto.setUser_id(user.getId());

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        requestDto.setComment(comment);

        likeService.Like(requestDto);

        return "redirect:board/article/" + articleId;
    }

    // 새글 작성 페이지
    @GetMapping("board/new")
    public String newArticle() {
        return "board/newArticle";
    }


    // 게시글 수정
    @GetMapping("board/article/update/{articleId}")
    public String updateArticle(@PathVariable Long articleId, Model model
            , @AuthenticationPrincipal CustomUserDetails currentUser) {

        GetArticleResponseDto responseDto = articleService.getArticleById(articleId);

        if (!responseDto.getWriterId().equals(currentUser.getId())) {
            MessageDto message = new MessageDto("작성자만 수정할 수 있습니다.", "board/article/" + articleId);
            model.addAttribute("data", message);
            return showMessageAndRedirect(message, model);
        }
        log.info(responseDto.getHeader());
        model.addAttribute("article", responseDto);

        return "board/updateArticle";
    }

    // 게시물(articleId) 삭제
    @GetMapping("delete/article/{articleId}")
    public String deleteArticle(@PathVariable Long articleId
            , @AuthenticationPrincipal CustomUserDetails user, Model model) {
        Long writerID = articleService.getArticleById(articleId).getWriterId();
        if (!writerID.equals(user.getId())) {
            MessageDto message = new MessageDto("작성자만 삭제할 수 있습니다.", "/board/article/" + articleId);
            model.addAttribute("data", message);
            return showMessageAndRedirect(message, model);

        }
        articleService.deleteArticle(articleId);
        MessageDto message = new MessageDto("삭제 완료.", "/board");
        model.addAttribute("data", message);
        return showMessageAndRedirect(message, model);
    }

    //게시글 숨김처리
    @GetMapping("article/hide")
    public String hideArticle(@RequestParam Long id) {
        articleService.hideArticle(id);
        return "redirect:/board";
    }
}