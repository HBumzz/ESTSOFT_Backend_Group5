package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.response.ChallengeViewResponse;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.service.ChallengeBoardService;
import com.app.salty.admin.challengeBoard.service.ChallengeCommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class ChallengeBoardPageController {

    public final ChallengeBoardService challengeBoardService;
    private final ChallengeCommentService challengeCommentService;

    public ChallengeBoardPageController(ChallengeBoardService challengeBoardService, ChallengeCommentService challengeCommentService) {
        this.challengeBoardService = challengeBoardService;
        this.challengeCommentService = challengeCommentService;
    }

    @GetMapping("/chboard")
    public String showChallenge(Model model) {
        // 각 챌린지 종류별 리스트를 조회하여 모델에 추가
        List<ChallengeViewResponse> dailyChallenges = challengeBoardService.getChallengesByType(Challenge.ChallengeType.DAILY)
                .stream()
                .map(ChallengeViewResponse::new)
                .toList();

        List<ChallengeViewResponse> weeklyChallenges = challengeBoardService.getChallengesByType(Challenge.ChallengeType.WEEKLY)
                .stream()
                .map(ChallengeViewResponse::new)
                .toList();

        List<ChallengeViewResponse> monthlyChallenges = challengeBoardService.getChallengesByType(Challenge.ChallengeType.MONTHLY)
                .stream()
                .map(ChallengeViewResponse::new)
                .toList();

        model.addAttribute("dailyChallenges", dailyChallenges);
        model.addAttribute("weeklyChallenges", weeklyChallenges);
        model.addAttribute("monthlyChallenges", monthlyChallenges);

        return "chboard/challengeList";  // challengeList.html
    }

    //GET 상세페이지 리턴
    @GetMapping("/chboard/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        Challenge challenge = challengeBoardService.findBy(id);
        model.addAttribute("challenge", new ChallengeViewResponse(challenge));
        List<ChallengeComment> comments = challengeCommentService.findCommentsByChallengeId(id);
        model.addAttribute("comments", comments);

        return "chboard/challenge";
    }

    //GET /new-challenge?id=1
    @GetMapping("/new-challenge")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("challenge", new ChallengeViewResponse());
        } else {
            Challenge challenge = challengeBoardService.findBy(id);
            model.addAttribute("challenge", new ChallengeViewResponse(challenge));
        }
        return "chboard/newChallenge";
    }



}
