package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.response.ChallengeViewResponse;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.service.ChallengeBoardService;
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

    public ChallengeBoardPageController(ChallengeBoardService challengeBoardService) {
        this.challengeBoardService = challengeBoardService;
    }

    @GetMapping("/chboard")
    public String showChallenge(Model model) {
        List<Challenge> challengeList = challengeBoardService.findAll();

        List<ChallengeViewResponse> list = challengeList.stream()
                .map(ChallengeViewResponse::new)
                .toList();

        model.addAttribute("challenges", list);

        return "/chboard/challengeList";  //challengeList.html
    }

    //GET 상세페이지 리턴
    @GetMapping("/chboard/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        Challenge challenge = challengeBoardService.findBy(id);
        model.addAttribute("challenge", new ChallengeViewResponse(challenge));

        return "/chboard/challenge";
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
        return "/chboard/newChallenge";
    }

}
