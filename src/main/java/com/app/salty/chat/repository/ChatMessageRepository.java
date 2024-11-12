package com.app.salty.chat.repository;

import com.app.salty.chat.entity.ChatMessage;
import com.app.salty.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom_Id(Long chatRoomId);
    Optional<ChatMessage> findFirstByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

}
