package org.example.chatservice.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.entity.Message;
import org.example.chatservice.domain.service.ChatService;
import org.example.chatservice.global.dto.ChatMessage;
import org.example.chatservice.global.vos.CustomOAuth2User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * @author jiyoung
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;

    @MessageMapping("/chats/{chatRoomId}")
    @SendTo("/sub/chats/{chatRoomId}")
    public ChatMessage handleMessage(@AuthenticationPrincipal Principal principal, @DestinationVariable Long chatRoomId, @Payload Map<String, String> payload) {
        log.info("{} sent {}", principal.getName(), payload, chatRoomId);

        CustomOAuth2User user = (CustomOAuth2User) ((OAuth2AuthenticationToken) principal).getPrincipal();
        Message message = chatService.saveMessage(user.getMember(), chatRoomId, payload.get("message"));
        return new ChatMessage(principal.getName(), payload.get("message"));
    }
}
