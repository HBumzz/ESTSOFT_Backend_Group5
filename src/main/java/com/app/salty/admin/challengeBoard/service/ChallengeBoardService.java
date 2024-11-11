package com.app.salty.admin.challengeBoard.service;

import com.app.salty.admin.challengeBoard.dto.request.AddChallengeRequest;
import com.app.salty.admin.challengeBoard.dto.request.UpdateChallengeRequest;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.repository.ChallengeBoardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChallengeBoardService {
    public final ChallengeBoardRepository repository;

    @Autowired
    public ChallengeBoardService(ChallengeBoardRepository repository) {
        this.repository = repository;
    }

    public Challenge saveChallenge(AddChallengeRequest request) {
        return repository.save(request.toEntity());
    }

    public List<Challenge> findAll() {
        return repository.findAll();
    }

    //blog 게시글 단건 조회 id(PK) 1건
    public Challenge findBy(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found id: " + id));
    }

    //blog 게시글 삭제 API
    public void deleteBy(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Challenge update(Long id, UpdateChallengeRequest request) {
        Challenge challenge = findBy(id);       // 수정하고싶은 article객체 가져오기
        challenge.update(request.getTitle(), request.getContent(), request.getStartDate(), request.getEndDate(), request.getStatus());
        return challenge;
    }

    public List<Challenge> getChallengesByType(Challenge.ChallengeType type) {
        return repository.findByType(type);
    }
}
