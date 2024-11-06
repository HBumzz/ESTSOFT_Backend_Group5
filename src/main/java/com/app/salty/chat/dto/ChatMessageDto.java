package com.app.salty.chat.dto;

import com.app.salty.user.entity.Users;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    public enum MessageType{
        ENTER, TALK
    }

    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId; // 채팅을 보낸 사람
    private String message;
}