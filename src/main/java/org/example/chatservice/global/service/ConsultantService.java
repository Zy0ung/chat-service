package org.example.chatservice.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.dto.ChatRoomDto;
import org.example.chatservice.domain.emum.Role;
import org.example.chatservice.domain.entity.ChatRoom;
import org.example.chatservice.domain.entity.Member;
import org.example.chatservice.domain.repository.ChatRoomRepository;
import org.example.chatservice.domain.repository.MemberRepository;
import org.example.chatservice.global.dto.MemberDto;
import org.example.chatservice.global.vos.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author jiyoung
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultantService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByName(username).get();

        if (Role.fromCode(member.getRole()) != Role.CONSULTANT) {
            throw new AccessDeniedException("상담사가 아닙니다.");
        }
        return new CustomUserDetails(member, null);
    }

    public MemberDto saveMember(MemberDto memberDto) {
        Member member = MemberDto.to(memberDto);
        member.updatePassword(memberDto.password(), memberDto.confirmedPassword(), passwordEncoder);

        member = memberRepository.save(member);

        return MemberDto.from(member);
    }

    public Page<ChatRoomDto> getChatRoomPage(Pageable pageable) {
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAll(pageable);

        return chatRoomPage.map(ChatRoomDto::from);
    }
}