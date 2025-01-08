package org.example.chatservice.domain.repository;

import org.example.chatservice.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jiyoung
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
