package org.example.chatservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author jiyoung
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MemberChatRoomMapping {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chat_room_mapping_id")
    @Id
    Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    Member member;

    @JoinColumn(name = "chat_room_id ")
    @ManyToOne
    ChatRoom chatRoom;
}
