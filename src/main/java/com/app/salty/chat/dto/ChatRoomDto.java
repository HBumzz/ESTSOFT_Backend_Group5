package com.app.salty.chat.dto;

import com.app.salty.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private Users user1;
    private Users user2;
    private LocalDateTime createdAt;
}
