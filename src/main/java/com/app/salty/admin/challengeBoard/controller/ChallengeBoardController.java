package com.app.salty.admin.challengeBoard.controller;

import com.app.salty.admin.challengeBoard.dto.request.AddChallengeRequest;
import com.app.salty.admin.challengeBoard.dto.request.UpdateChallengeRequest;
import com.app.salty.admin.challengeBoard.dto.response.ChallengeResponse;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.service.ChallengeBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChallengeBoardController {

    private final ChallengeBoardService service;

    public ChallengeBoardController(ChallengeBoardService service) {
        this.service = service;
    }

    // 챌린지 글 작성
    @PostMapping("/chboard/write")
    public ResponseEntity<ChallengeResponse> writeArticle(@RequestBody AddChallengeRequest request) {
        // request에서 받은 데이터를 서비스로 전달해 챌린지 저장
        Challenge challenge = service.saveChallenge(request);

        return ResponseEntity.status(HttpStatus.CREATED) // 201 응답
                .body(challenge.convert()); // 챌린지 객체를 응답 형식으로 변환
    }

    // 챌린지 모두 조회
    @GetMapping("/chboard")
    public ResponseEntity<List<ChallengeResponse>> findAll() {
        List<Challenge> challengeList = service.findAll();  // 서비스로 모든 챌린지 조회
        List<ChallengeResponse> list = challengeList.stream()
                .map(Challenge::convert) // 각 챌린지 객체 응답 형식으로 변환
                .toList();
        return ResponseEntity.ok(list); // 200 응답
    }

    // 챌린지 단건 조회
    @GetMapping("/chboard/{id}")
    public ResponseEntity<ChallengeResponse> fineById(@PathVariable Long id) {
        Challenge challenge = service.findBy(id); // ID로 챌린지 조회
        return ResponseEntity.ok(challenge.convert());
    }

    // 챌린지 삭제
    @DeleteMapping("/chboard/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteBy(id);
        return ResponseEntity.ok().build();
    }

    // 챌린지 수정
    @PutMapping("/chboard/{id}")
    public ResponseEntity<ChallengeResponse> updateById(@PathVariable Long id,
                                                        @RequestBody UpdateChallengeRequest request) {
        // 수정된 챌린지 정보 업데이트
        Challenge updatedChallenge = service.update(id, request);

        return ResponseEntity.ok(updatedChallenge.convert());
    }

}
