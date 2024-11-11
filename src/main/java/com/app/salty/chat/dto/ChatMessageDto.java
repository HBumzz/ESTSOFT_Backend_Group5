package com.app.salty.chat.dto;
import java.time.LocalDateTime;

import com.app.salty.user.dto.response.UsersResponse;
import lombok.*;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    public enum MessageType {
        ENTER, TALK
    }

    private Long messageId;
    private Long chatRoomId;
    private UsersResponse sender;
    private UsersResponse receiver;
    private String message;
    private MessageType messageType;
    private LocalDateTime createdAt;

}
