package com.app.salty.board.controller.view;

import com.app.salty.board.dto.article.GetArticleResponseDto;
import com.app.salty.board.dto.article.SaveArticleRequestDto;
import com.app.salty.board.service.ArticleServiceImpl;
import com.app.salty.user.entity.Users;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.app.salty.board.entity.QArticle.article;

@Controller
public class BoardPageController {

    ArticleServiceImpl articleService;

    BoardPageController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    // 게시물 전체 조회 -> 게시판 첫 화면
    @GetMapping("/board")
    public String boardIndex(Model model) {
        List<GetArticleResponseDto> dtoList = articleService.getArticleList();

        model.addAttribute("articles", dtoList);
        return "board/articleList";

    }

    // 게시물 상세 보기
    @GetMapping("/articles/{id}")
    public String showArticle(Model model, @PathVariable Long id, @AuthenticationPrincipal Users user) {
        // @AuthenticationPrincipal로 Users객체를 받으면 Users객체에 대한 내용만 조회가 됨

        // 권한 및 인증에 대한 조회 아래와 같이 하면 권한부분까지 조회가 가능 -> security끄고 테스트중 널포인트 발생해서 꺼둠.
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Users principal = (Users) authentication.getPrincipal();

        GetArticleResponseDto responseDto = articleService.getArticleById(id);

        model.addAttribute("article",responseDto);

        return "board/showArticle";
    }


    // 게시물 등록 또는 수정
    @GetMapping("/new-article")
    public String addArticle(@RequestParam(required = false) Long id, Model model) {
        if(id == null) { // id가 없다? 새로운 게시글 생성
            model.addAttribute("article", new SaveArticleRequestDto());  // 빈객체를 보내줌
        } else {        // 게시글 수정
            GetArticleResponseDto responseDto = articleService.getArticleById(id);
            model.addAttribute("article",responseDto);
        }

        return "board/newArticle";
    }

}
