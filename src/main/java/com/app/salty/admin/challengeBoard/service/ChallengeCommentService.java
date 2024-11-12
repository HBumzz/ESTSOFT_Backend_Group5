package com.app.salty.admin.challengeBoard.service;

import com.app.salty.admin.challengeBoard.dto.request.CommentRequestDTO;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.repository.ChallengeBoardRepository;
import com.app.salty.admin.challengeBoard.repository.ChallengeCommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ChallengeCommentService {
    private final ChallengeBoardRepository blogRepository;
    private final ChallengeCommentRepository commentRepository;

    public ChallengeCommentService(ChallengeBoardRepository blogRepository, ChallengeCommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
    }

    // 댓글 저장
    public ChallengeComment saveComment(Long challengeId, CommentRequestDTO requestDTO) {
        // 해당 챌린지 찾기
        Challenge challenge = blogRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found")); // 예외 처리 추가

        // 댓글 저장
        ChallengeComment comment = new ChallengeComment(requestDTO.getBody(), challenge);
        return commentRepository.save(comment);
    }

    // 댓글 조회
    public ChallengeComment findComment(Long commentId) {
        Optional<ChallengeComment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    public List<ChallengeComment> findCommentsByChallengeId(Long challengeId) {
        return commentRepository.findByChallengeId(challengeId);
    }

    // 댓글 수정
    public ChallengeComment update(Long commentId, CommentRequestDTO request) {
        ChallengeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 예외 처리 추가

        // 댓글 내용 수정
        comment.updateCommentBody(request.getBody());
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void delete(Long commentId) {
        ChallengeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 예외 처리 추가
        commentRepository.delete(comment); // 객체를 직접 삭제
    }


}
