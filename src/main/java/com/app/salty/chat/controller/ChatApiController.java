package com.app.salty.chat.controller;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {
    private static final Logger logger = LoggerFactory.getLogger(ChatApiController.class);
    private final ChatService chatService;

    @PostMapping("/rooms")
    public RedirectView createRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Long roomId;
        try {
            ChatRoom chatRoom = chatService.createRoom(user1Id, user2Id);
            roomId = chatRoom.getId();
            logger.info("New chat room created with ID: " + roomId);
        } catch (IllegalStateException e) {
            // 이미 존재하는 채팅방의 ID 가져오기
            roomId = chatService.getExistingRoomId(user1Id, user2Id);
            logger.info("Existing chat room found with ID: " + roomId);
        }

        // roomId가 올바르게 설정되었는지 확인
        logger.info("Redirecting to chat with roomId: " + roomId);

        // 채팅방이 이미 존재하더라도 roomId로 redirect
        RedirectView redirectView = new RedirectView("/chat");
        redirectView.addStaticAttribute("roomId", roomId);
        return redirectView;
    }

    @GetMapping("/rooms/id")
    public ResponseEntity<Long> getRoomId(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Long roomId = chatService.getExistingRoomId(user1Id, user2Id);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/rooms")
    public List<ChatRoomDto> getChatRoomsByUser(@RequestParam Long userId) {
        return chatService.getChatRoomsByUser(userId);
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto chatMessageDto) {
        ChatMessageDto savedMessage = chatService.sendMessage(chatMessageDto);
        return ResponseEntity.ok(savedMessage);
    }


    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessagesByChatRoom(@PathVariable Long chatRoomId) {
        List<ChatMessageDto> messages = chatService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages); // 채팅 메시지 리스트 반환
    }
}
