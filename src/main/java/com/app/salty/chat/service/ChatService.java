package com.app.salty.chat.service;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatMessage;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.repository.ChatMessageRepository;
import com.app.salty.chat.repository.ChatRoomRepository;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatRoomDto convertToChatRoomDto(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getUser1(),    // user1의 모든 정보
                chatRoom.getUser2(),    // user2의 모든 정보
                chatRoom.getCreatedAt()
        );
    }
    public List<ChatRoomDto> getChatRoomsByUser(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUser1_IdOrUser2_Id(userId, userId);
        return chatRooms.stream()
                .map(this::convertToChatRoomDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoom createRoom(Long user1Id, Long user2Id) {
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByUser1_IdAndUser2_IdOrUser2_IdAndUser1_Id(user1Id, user2Id, user1Id, user2Id);

        if (existingChatRoom.isPresent()) {
            throw new IllegalStateException("Chat room already exists between these users.");
        }

        Users user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user1Id));
        Users user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user2Id));

        ChatRoom chatRoom = ChatRoom.builder()
                .user1(user1)
                .user2(user2)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatMessage sendMessage(ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found: " + chatMessageDto.getChatRoomId()));

        Users sender = userRepository.findById(chatMessageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + chatMessageDto.getSenderId()));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(chatMessageDto.getMessage())
                .messageType(ChatMessage.MessageType.valueOf(chatMessageDto.getMessageType().name()))
                .build();

        return chatMessageRepository.save(chatMessage);
    }



    @Transactional(readOnly = true)
    public List<ChatMessage> getMessagesByChatRoom(Long chatRoomId) {
        return chatMessageRepository.findAllByChatRoom_Id(chatRoomId);
    }
}
