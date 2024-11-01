package com.app.salty.user.entity;

import com.app.salty.user.common.AuthProvider;
import com.app.salty.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Table(name = "social_providers")
@NoArgsConstructor
public class SocialProvider extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    // 자주 사용하고 중요한 필드는 컬럼으로 관리
    private String nickname;
    private String profileImage;

    // 부가적인 정보는 JSON으로 저장
//    @Convert(converter = JsonConverter.class)
//    @Column(columnDefinition = "json")
//    private Map<String, Object> additionalData = new HashMap<>();

    //생성 메서드
    @Builder
    public SocialProvider( AuthProvider provider, String providerId, String nickname, String profileImage) {
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.profileImage = profileImage;

    }

    @Override
    public String toString() {
        return "SocialProvider{" +
                "id=" + id +
                ", user=" + user +
                ", provider=" + provider +
                ", providerId='" + providerId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}