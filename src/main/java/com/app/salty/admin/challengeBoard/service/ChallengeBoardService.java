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
    private final ChallengeBoardRepository repository;

    @Autowired
    public ChallengeBoardService(ChallengeBoardRepository repository) {
        this.repository = repository;
    }

    // 챌린지 생성
    public Challenge saveChallenge(AddChallengeRequest request) {
        // AddChallengeRequest로부터 Challenge 엔티티를 생성하여 저장
        return repository.save(request.toEntity());
    }

    // 모든 챌린지 조회
    public List<Challenge> findAll() {
        return repository.findAll();
    }

    // ID로 챌린지 단건 조회
    public Challenge findBy(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found id: " + id));
    }

    // 챌린지 삭제
    public void deleteBy(Long id) {
        repository.deleteById(id);
    }

    // 챌린지 수정
    @Transactional
    public Challenge update(Long id, UpdateChallengeRequest request) {
        // 수정할 챌린지 가져오기
        Challenge challenge = findBy(id);

        // 수정된 값으로 챌린지 업데이트
        challenge.update(request.getTitle(), request.getContent(), request.getStartDate(), request.getEndDate(), request.getStatus(), request.getType());

        // 수정된 챌린지 저장
        return repository.save(challenge);
    }

    // 타입별 챌린지 조회
    public List<Challenge> getChallengesByType(Challenge.ChallengeType type) {
        return repository.findByType(type);
    }
}
