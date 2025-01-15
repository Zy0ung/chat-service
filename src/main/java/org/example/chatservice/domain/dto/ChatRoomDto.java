package org.example.chatservice.domain.dto;

import java.time.LocalDateTime;
import org.example.chatservice.domain.entity.ChatRoom; /**
 * @author jiyoung
 */
public record ChatRoomDto(
        Long id,
        String title,
        Boolean hasNewMessage,
        Integer memberCount,
        LocalDateTime createdAt) {

    public static ChatRoomDto from (ChatRoom chatRoom){
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getTitle(), chatRoom.getHasNewMessage(),
                chatRoom.getMemberChatRoomMappingSet().size(), chatRoom.getCreatedAt());
    }
}
