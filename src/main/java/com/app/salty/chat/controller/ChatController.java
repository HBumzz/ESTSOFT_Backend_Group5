package com.app.salty.chat.controller;

import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatMessage;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
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
    public String chatMain(Model model, @RequestParam Long userId) {
        log.info("Received userId: " + userId);
        List<ChatRoomDto> chatRooms = chatService.getChatRoomsByUser(userId);
        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("userId", userId);
        return "/chat/chat";
    }

    @GetMapping("/rooms/{chatRoomId}")
    public String chatRoom(@PathVariable Long chatRoomId, Model model) {
        List<ChatMessage> messages = chatService.getMessagesByChatRoom(chatRoomId);
        model.addAttribute("messages", messages);
        model.addAttribute("chatRoomId", chatRoomId);
        return "chat/html/chatroom";
    }
}

