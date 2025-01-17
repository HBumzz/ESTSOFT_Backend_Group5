package com.app.salty.chat.controller;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Tag(name = "Salty - 채팅 관련 API")
@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatApiController {
    private static final Logger logger = LoggerFactory.getLogger(ChatApiController.class);
    private final ChatService chatService;


    @Operation(summary = "채팅방 생성 API")
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

    @Operation(summary = "유저1, 유저2의 채팅방 조회")
    @GetMapping("/rooms/id")
    public ResponseEntity<Long> getRoomId(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Long roomId = chatService.getExistingRoomId(user1Id, user2Id);
        return ResponseEntity.ok(roomId);
    }

    @Operation(summary = "유저의 채팅방 리스트 조회")
    @GetMapping("/rooms")
    public List<ChatRoomDto> getChatRoomsByUser(@RequestParam Long userId) {
        return chatService.getChatRoomsByUser(userId);
    }

    @Operation(summary = "채팅 메세지 보내기")
    @PostMapping("/message")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto chatMessageDto) {
        ChatMessageDto savedMessage = chatService.sendMessage(chatMessageDto);
        return ResponseEntity.ok(savedMessage);
    }

    @Operation(summary = "채팅방 메세지 조회")
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessagesByChatRoom(@PathVariable Long chatRoomId) {
        List<ChatMessageDto> messages = chatService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages); // 채팅 메시지 리스트 반환
    }
}
