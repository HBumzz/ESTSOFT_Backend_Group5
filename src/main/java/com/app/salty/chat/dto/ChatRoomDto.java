package com.app.salty.chat.dto;

import com.app.salty.user.dto.response.UsersResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private UsersResponse user1;
    private UsersResponse user2;
    private LocalDateTime createdAt;
}
