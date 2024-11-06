package com.app.salty.chat.repository;

import com.app.salty.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);

    Optional<ChatRoom> findByUser1_IdAndUser2_IdOrUser2_IdAndUser1_Id(Long user1Id, Long user2Id, Long user2IdReversed, Long user1IdReversed);
}