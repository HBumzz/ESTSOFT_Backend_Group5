package com.app.salty.chat.entity;

import com.app.salty.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "activated", "createdAt", "updatedAt"})
    private Users sender;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
//        log.info("onCreate 호출됨, createdAt 값: {}", this.createdAt);
    }

    @Builder
    public ChatMessage(ChatRoom chatRoom, Users sender, String message, MessageType messageType, LocalDateTime createdAt) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();  // Null 체크 후 기본값 설정
//        log.info("ChatMessage 빌더 호출됨, createdAt 값: {}", this.createdAt);
    }

    public enum MessageType {
        ENTER,
        TALK
    }
}