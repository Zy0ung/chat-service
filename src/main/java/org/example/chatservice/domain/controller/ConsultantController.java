package org.example.chatservice.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.dto.ChatRoomDto;
import org.example.chatservice.global.dto.MemberDto;
import org.example.chatservice.global.service.ConsultantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jiyoung
 */
@Slf4j
@RequestMapping("/consultants")
@RequiredArgsConstructor
@Controller
public class ConsultantController {

    private final ConsultantService consultantService;

    @ResponseBody
    @PostMapping
    public MemberDto saveMember(@RequestBody MemberDto memberDto) {
        return consultantService.saveMember(memberDto);
    }

    @GetMapping
    public String index() {
        return "consultants/index.html";
    }

    @GetMapping("/chats")
    @ResponseBody
    public Page<ChatRoomDto> getChatRoomPage(Pageable pageable) {
        return consultantService.getChatRoomPage(pageable);
    }
}