package com.app.salty.chat.entity;

import com.app.salty.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "activated", "createdAt", "updatedAt"})
    private Users user1; // 본인

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "activated", "createdAt", "updatedAt"})
    private Users user2; // 상대방

    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @Builder
    public ChatRoom(Users user1, Users user2, LocalDateTime createdAt) {
        this.user1 = user1;
        this.user2 = user2;
        this.createdAt = createdAt;
    }
}

