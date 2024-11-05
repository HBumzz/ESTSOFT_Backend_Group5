package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.request.AddChallengeRequest;
import com.app.salty.admin.challengeBoard.dto.response.ChallengeResponse;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.service.ChallengeBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChallengeBoardController {

    private final ChallengeBoardService service;

    public ChallengeBoardController(ChallengeBoardService service) {
        this.service = service;
    }

    @PostMapping("/chboard/write")
    public ResponseEntity<ChallengeResponse> writeArticle(@RequestBody AddChallengeRequest request) {
        log.info(request.getTitle(), request.getContent());
        Challenge challenge = service.saveChallenge(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challenge.convert());
    }



}
