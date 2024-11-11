package com.app.salty.chat.service;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.dto.ChatRoomDto;
import com.app.salty.chat.entity.ChatMessage;
import com.app.salty.chat.entity.ChatRoom;
import com.app.salty.chat.repository.ChatMessageRepository;
import com.app.salty.chat.repository.ChatRoomRepository;
import com.app.salty.user.dto.response.ProfileResponse;
import com.app.salty.user.dto.response.UsersResponse;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        UsersResponse user1Response = UsersResponse.builder()
                .userId(chatRoom.getUser1().getId())
                .email(chatRoom.getUser1().getEmail())
                .nickname(chatRoom.getUser1().getNickname())
                .profile(
                        chatRoom.getUser1().getProfile() != null
                                ? ProfileResponse.builder()
                                .type(chatRoom.getUser1().getProfile().getId().getType().toString()) // AttachmentType이 enum이라면 String으로 변환
                                .id(chatRoom.getUser1().getProfile().getId().getUserId())
                                .originalFilename(chatRoom.getUser1().getProfile().getOriginalFilename())
                                .path(chatRoom.getUser1().getProfile().getPath())
                                .build()
                                : ProfileResponse.builder()
                                .path("/images/default-profile.png") // 기본 프로필 이미지 설정
                                .build()
                )
                .build();

        UsersResponse user2Response = UsersResponse.builder()
                .userId(chatRoom.getUser2().getId())
                .email(chatRoom.getUser2().getEmail())
                .nickname(chatRoom.getUser2().getNickname())
                .profile(
                        chatRoom.getUser2().getProfile() != null
                                ? ProfileResponse.builder()
                                .type(chatRoom.getUser2().getProfile().getId().getType().toString())
                                .id(chatRoom.getUser2().getProfile().getId().getUserId())
                                .originalFilename(chatRoom.getUser2().getProfile().getOriginalFilename())
                                .path(chatRoom.getUser2().getProfile().getPath())
                                .build()
                                : ProfileResponse.builder()
                                .path("/images/default-profile.png")
                                .build()
                )
                .build();

        return new ChatRoomDto(
                chatRoom.getId(),
                user1Response,
                user2Response,
                chatRoom.getCreatedAt()
        );
    }
    public List<ChatRoomDto> getChatRoomsByUser(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUser1_IdOrUser2_Id(userId, userId);
        return chatRooms.stream()
                .map(chatRoom -> {
                    // 현재 userId가 user1인지 확인
                    if (chatRoom.getUser1().getId().equals(userId)) {
                        // userId가 user1일 경우 그대로 설정
                        return convertToChatRoomDto(chatRoom);
                    } else {
                        // userId가 user2일 경우 user1과 user2를 바꿔 설정
                        ChatRoomDto chatRoomDto = convertToChatRoomDto(chatRoom);
                        UsersResponse tempUser = chatRoomDto.getUser1();
                        chatRoomDto.setUser1(chatRoomDto.getUser2());
                        chatRoomDto.setUser2(tempUser);
                        return chatRoomDto;
                    }
                })
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
    public ChatMessageDto sendMessage(ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found: " + chatMessageDto.getChatRoomId()));

        Users sender = userRepository.findById(chatMessageDto.getSender().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + chatMessageDto.getSender().getUserId()));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(chatMessageDto.getMessage())
                .messageType(ChatMessage.MessageType.valueOf(chatMessageDto.getMessageType().name()))
                .createdAt(LocalDateTime.now())
                .build();
//        log.info("메시지 전송, ChatMessage createdAt: {}", chatMessage.getCreatedAt());
        chatMessageRepository.save(chatMessage);

        return ChatMessageDto.builder()
                .messageId(chatMessage.getMessageId())
                .chatRoomId(chatRoom.getId())
                .sender(UsersResponse.builder()
                        .userId(sender.getId())
                        .nickname(sender.getNickname())
                        .build())
                .receiver(UsersResponse.builder()
                        .userId(chatRoom.getUser2().getId())
                        .nickname(chatRoom.getUser2().getNickname())
                        .build())
                .message(chatMessage.getMessage())
                .messageType(ChatMessageDto.MessageType.valueOf(chatMessage.getMessageType().name()))
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }


    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessagesByChatRoom(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom_Id(chatRoomId);

        return messages.stream().map(message -> ChatMessageDto.builder()
                .messageId(message.getMessageId())
                .chatRoomId(message.getChatRoom().getId())
                .sender(UsersResponse.builder()
                        .userId(message.getSender().getId())
                        .nickname(message.getSender().getNickname())
                        .build())
                .receiver(UsersResponse.builder()
                        .userId(message.getChatRoom().getUser2().getId())  // 또는 user1을 receiver로 설정할 경우 user1로 설정
                        .nickname(message.getChatRoom().getUser2().getNickname())
                        .build())
                .message(message.getMessage())
                .messageType(ChatMessageDto.MessageType.valueOf(message.getMessageType().name()))
                .createdAt(message.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }
}
