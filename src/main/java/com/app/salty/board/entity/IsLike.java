package com.app.salty.board.entity;

import com.app.salty.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_id")
    private Long id;

    // user, contentType, contentId 를 기본키로 추가할 것 -> 중복 방지

    @ManyToOne
    @JoinColumn(name ="user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name="content_type", nullable = false)
    private LikeContentType contentType;

    @Column(name="content_id")
    private Long contentId;

    public IsLike(Users user, LikeContentType contentType, Long contentId) {
        this.user=user;
        this.contentType=contentType;
        this.contentId = contentId;
    }
}
