package org.example.chatservice.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.entity.ChatRoom;
import org.example.chatservice.domain.entity.Member;
import org.example.chatservice.domain.entity.MemberChatRoomMapping;
import org.example.chatservice.domain.repository.ChatRoomRepository;
import org.example.chatservice.domain.repository.MemberChatRoomMappingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jiyoung
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomMappingRepository memberChatRoomMappingRepository;

    /**
     * 채팅방 개설
     */
    public ChatRoom createChatRoom(Member member, String title) {
        ChatRoom chatroom = ChatRoom.builder()
                                    .title(title)
                                    .createAt(LocalDateTime.now())
                                    .build();
        chatroom = chatRoomRepository.save(chatroom);

        MemberChatRoomMapping memberChatRoomMapping = chatroom.addMember(member);

        memberChatRoomMapping = memberChatRoomMappingRepository.save(memberChatRoomMapping);

        return chatroom;
    }

    /**
     * 채팅방 참여
     */
    public Boolean joinChatRoom(Member member, Long chatRoomId) {

        if(memberChatRoomMappingRepository.existsByMemberIdAndChatRoomId(member.getId(), chatRoomId)) {
            log.info("이미 참여한 채팅방입니다.");
            return false;
        }

        ChatRoom chatroom = chatRoomRepository.findById(chatRoomId).get();

        MemberChatRoomMapping memberChatRoomMapping = MemberChatRoomMapping.builder()
                                                    .member(member)
                                                    .chatRoom(chatroom)
                                                    .build();

        memberChatRoomMapping = memberChatRoomMappingRepository.save(memberChatRoomMapping);

        return true;
    }

    /**
     * 채팅방 나가기
     */
    @Transactional
    public Boolean leaveChatRoom(Member member, Long chatRoomId) {
        if (!memberChatRoomMappingRepository.existsByMemberIdAndChatRoomId(member.getId(), chatRoomId)){
            log.info("참여하지 않은 방입니다.");
            return false;
        }
        memberChatRoomMappingRepository.deleteByMemberIdAndChatRoomId(member.getId(), chatRoomId);

        return true;
    }

    /**
     * 참여한 채팅방 목록
     */
    public List<ChatRoom> getChatRoomList(Member member) {

        List<MemberChatRoomMapping> memberChatRoomMappingList =
                memberChatRoomMappingRepository.findAllByMemberId(member.getId());

        return memberChatRoomMappingList.stream().map(MemberChatRoomMapping::getChatRoom).toList();
    }
}