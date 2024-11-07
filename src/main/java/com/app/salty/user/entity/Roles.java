package com.app.salty.user.entity;

import com.app.salty.user.common.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
public class Roles {

    @Id
    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int level;

    @Builder
    public Roles(Role role, String description, int level) {
        this.role = role;
        this.description = description;
        this.level = level;
    }

    public boolean hasAuthorityGreaterOrEqual(Roles other) {
        return this.level >= other.getLevel();
    }
}
