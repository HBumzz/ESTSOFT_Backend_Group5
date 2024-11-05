package com.app.salty.user.entity;

import com.app.salty.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_role_mapping")
public class UserRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_mapping_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_name", nullable = false)
    private Roles role;

    @Builder
    public UserRoleMapping(Users user, Roles role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserRoleMapping{" +
                "id=" + id +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}
