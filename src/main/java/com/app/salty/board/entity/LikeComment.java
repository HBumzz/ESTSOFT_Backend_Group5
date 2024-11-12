package com.app.salty.board.entity;

import com.app.salty.user.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class LikeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private Comment comment;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public LikeComment(Comment comment, Long userId) {
        this.comment=comment;
        this.userId=userId;
    }
}
