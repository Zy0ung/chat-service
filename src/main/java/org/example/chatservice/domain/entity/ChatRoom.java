package org.example.chatservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author jiyoung
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ChatRoom {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    @Id
    Long id;

    String title;

    LocalDateTime createAt;

    @OneToMany(mappedBy = "chatRoom")
    Set<MemberChatRoomMapping> memberChatRoomMappingSet;
}