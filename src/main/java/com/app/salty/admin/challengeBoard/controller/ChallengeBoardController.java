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

    //챌린지 글 작성
    @PostMapping("/chboard/write")
    public ResponseEntity<ChallengeResponse> writeArticle(@RequestBody AddChallengeRequest request) { //json데이터를 자바객체로 받아오는 어노테이션
        Challenge challenge = service.saveChallenge(request); //서비스 메서드로 챌린지 저장

        return ResponseEntity.status(HttpStatus.CREATED) //201 응답받고 챌린지 정보 반환
                .body(challenge.convert()); //챌린지 객체를 응답 형식으로 변환 저장
    }

    //챌린지 모두 조회
    @GetMapping("/chboard")
    public ResponseEntity<List<ChallengeResponse>> findAll() {
        List<Challenge> challengeList = service.findAll();  //서비스로 모든 챌린지 조회
        List<ChallengeResponse> list = challengeList.stream()
                .map(Challenge::convert) //각 챌린지 객체 응답 형식으로 변환
                .toList();
        return ResponseEntity.ok(list); //200응답 받고 변환된 목록 반환
    }

    //챌린지 단건 조회
    @GetMapping("/chboard/{id}")
    public ResponseEntity<ChallengeResponse> fineById(@PathVariable Long id) { //{}안에 있는 값 변수로 받아오는 어노테이션
        Challenge challenge = service.findBy(id); //id해당하는 챌린지 조회
        return ResponseEntity.ok(challenge.convert());
    }

    //삭제
    @DeleteMapping("/chboard/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteBy(id);
        return ResponseEntity.ok().build();
    }

    //수정
    @PutMapping("/chboard/{id}")
    public ResponseEntity<ChallengeResponse> updateById(@PathVariable Long id,
                                                      @RequestBody UpdateChallengeRequest request) {
        Challenge updatedChallenge = service.update(id, request);
        return ResponseEntity.ok(updatedChallenge.convert());
    }


}
