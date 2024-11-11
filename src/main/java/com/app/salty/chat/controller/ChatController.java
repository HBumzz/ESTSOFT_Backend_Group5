package com.app.salty.chat.controller;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatMessage;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.service.ChatService;
import com.app.salty.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @GetMapping("/chat")
    public String chatMain(Model model,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();
        log.info("id값: {}", userId);
        model.addAttribute("user", customUserDetails);

        List<ChatRoomDto> chatRooms = chatService.getChatRoomsByUser(userId);
        model.addAttribute("chatRooms", chatRooms);

        return "/chat/chat";
    }

    @GetMapping("/rooms/{chatRoomId}")
    public String chatRoom(@PathVariable Long chatRoomId, Model model) {
        List<ChatMessageDto> messages = chatService.getMessagesByChatRoom(chatRoomId); // 변경된 부분
        model.addAttribute("messages", messages);
        model.addAttribute("chatRoomId", chatRoomId);
        return "chat/html/chatroom";
    }
}

