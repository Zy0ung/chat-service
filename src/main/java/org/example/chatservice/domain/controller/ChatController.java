package org.example.chatservice.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.dto.ChatRoomDto;
import org.example.chatservice.domain.entity.ChatRoom;
import org.example.chatservice.domain.service.ChatService;
import org.example.chatservice.global.vos.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jiyoung
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 생성
     */
    @PostMapping
    public ChatRoomDto createChatRoom(@AuthenticationPrincipal CustomOAuth2User user, @RequestParam String title) {
        ChatRoom chatRoom = chatService.createChatRoom(user.getMember(), title);
        return ChatRoomDto.from(chatRoom);
    }

    /**
     * 채팅방 참여
     */
    @PostMapping("/{chatRoomId}")
    public Boolean joinChatRoom(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable Long chatRoomId) {
        return chatService.joinChatRoom(user.getMember(), chatRoomId);
    }

    /**
     * 채팅방 나가기
     */
    @DeleteMapping("/{chatRoomId}")
    public Boolean leaveChatRoom(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable Long chatRoomId) {
        return chatService.leaveChatRoom(user.getMember(), chatRoomId);
    }

    /**
     * 참여 채팅방 조회
     */
    @GetMapping
    public List<ChatRoomDto> getChatRooms(@AuthenticationPrincipal CustomOAuth2User user) {
        List<ChatRoom> chatRoomList = chatService.getChatRoomList(user.getMember());

        return chatRoomList.stream().map(ChatRoomDto::from).toList();
    }
}
