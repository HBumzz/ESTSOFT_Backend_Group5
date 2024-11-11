package com.app.salty.admin.challengeBoard.service;

import com.app.salty.admin.challengeBoard.dto.request.CommentRequestDTO;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.entity.ChallengeComment;
import com.app.salty.admin.challengeBoard.repository.ChallengeBoardRepository;
import com.app.salty.admin.challengeBoard.repository.ChallengeCommentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChallengeCommentService {
    private final ChallengeBoardRepository blogRepository;

    private final ChallengeCommentRepository commentRepository;

    public ChallengeCommentService(ChallengeBoardRepository blogRepository, ChallengeCommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
    }
    public ChallengeComment saveComment(Long articleId, CommentRequestDTO requestDTO) {
        Challenge challenge = blogRepository.findById(articleId).orElseThrow(); //NoSuchElementException
        return commentRepository.save(new ChallengeComment(requestDTO.getBody(), challenge));
    }
    public ChallengeComment findComment(Long commentId) {
        Optional<ChallengeComment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(new ChallengeComment());
    }
    public ChallengeComment update(Long commentId, CommentRequestDTO request) {
        ChallengeComment comment = commentRepository.findById(commentId).orElseThrow();
        // 수정
        comment.updateCommentBody(request.getBody());
        return commentRepository.save(comment);
    }
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);   // delete from comment where id = ${commentId}
    }
}
