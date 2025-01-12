package org.example.chatservice.global.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.global.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * @author jiyoung
 */
@Slf4j
@Controller
public class StompChatController {

    @MessageMapping("/chats/{chatRoomId}")
    @SendTo("/sub/chats/{chatRoomId}")
    public ChatMessage handleMessage(@AuthenticationPrincipal Principal principal, @DestinationVariable Long chatRoomId, @Payload Map<String, String> payload) {
        log.info("{} sent {}", principal.getName(), payload, chatRoomId);
        return new ChatMessage(principal.getName(), payload.get("message"));
    }
}
