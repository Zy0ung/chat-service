package org.example.chatservice.domain.entity;

import jakarta.persistence.*;
import java.util.HashSet;
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

    LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom")
    Set<MemberChatRoomMapping> memberChatRoomMappingSet;

    // 클래스의 속성으로만 존재
    @Transient
    Boolean hasNewMessage;

    public void setHasNewMessage(Boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public MemberChatRoomMapping addMember(Member member){
        if(this.getMemberChatRoomMappingSet() == null){
            this.memberChatRoomMappingSet = new HashSet<>();
        }

        MemberChatRoomMapping memberChatRoomMapping = MemberChatRoomMapping.builder()
                                                                            .member(member)
                                                                            .chatRoom(this)
                                                                            .build();

        this.getMemberChatRoomMappingSet().add(memberChatRoomMapping);

        return memberChatRoomMapping;
    }
}