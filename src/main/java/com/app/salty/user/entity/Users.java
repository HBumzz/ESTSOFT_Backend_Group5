package com.app.salty.user.entity;

import com.app.salty.util.BaseTimeEntity;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // 임의로 사용자 만들기위해서 넣었습니다.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private boolean activated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserRoleMapping> userRoleMappings = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SocialProvider socialProvider;  // 소셜 로그인 정보와 1:1 관계

    //연관 관계 method
    public void addRoleMappings(UserRoleMapping roleMapping) {
        this.userRoleMappings.add(roleMapping);
    }


    //business method
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateActivated(boolean newActivated) {
        this.activated = newActivated;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", created='" + getCreatedAt() + '\'' +
                ", update='" + getUpdatedAt() + '\'' +
                ", activated=" + activated +
                '}';
    }
}
