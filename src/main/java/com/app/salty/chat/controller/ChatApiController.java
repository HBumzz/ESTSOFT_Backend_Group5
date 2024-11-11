package com.app.salty.chat.controller;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        ChatRoom chatRoom = chatService.createRoom(user1Id, user2Id);
        return ResponseEntity.ok(chatRoom);
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
