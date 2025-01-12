package org.example.chatservice.domain.dto;

import java.time.LocalDateTime;
import org.example.chatservice.domain.entity.ChatRoom; /**
 * @author jiyoung
 */
public record ChatRoomDto(
        Long id,
        String title,
        Integer memberCount,
        LocalDateTime createAt) {

    public static ChatRoomDto from (ChatRoom chatRoom){
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getTitle(), chatRoom.getMemberChatRoomMappingSet().size(),
                chatRoom.getCreateAt());
    }
}
