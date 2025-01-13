package org.example.chatservice.domain.repository;

import java.util.List;
import org.example.chatservice.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jiyoung
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatRoomId(Long chatRoomId);
}
