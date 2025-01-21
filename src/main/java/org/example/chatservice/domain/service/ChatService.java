package org.example.chatservice.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.domain.dto.ChatRoomDto;
import org.example.chatservice.domain.entity.ChatRoom;
import org.example.chatservice.domain.entity.Member;
import org.example.chatservice.domain.entity.MemberChatRoomMapping;
import org.example.chatservice.domain.entity.Message;
import org.example.chatservice.domain.repository.ChatRoomRepository;
import org.example.chatservice.domain.repository.MemberChatRoomMappingRepository;
import org.example.chatservice.domain.repository.MessageRepository;
import org.springframework.stereotype.Service;
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
    private final MessageRepository messageRepository;

    /**
     * 채팅방 개설
     */
    public ChatRoom createChatRoom(Member member, String title) {
        ChatRoom chatroom = ChatRoom.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
        chatroom = chatRoomRepository.save(chatroom);

        MemberChatRoomMapping memberChatRoomMapping = chatroom.addMember(member);

        memberChatRoomMapping = memberChatRoomMappingRepository.save(memberChatRoomMapping);

        return chatroom;
    }

    /**
     * 채팅방 참여
     */
    public Boolean joinChatRoom(Member member, Long newChatRoomId, Long currentChatRoomId) {

        if (currentChatRoomId != null) {
            updateLastCheckedAt(member, currentChatRoomId);
        }

        if (memberChatRoomMappingRepository.existsByMemberIdAndChatRoomId(member.getId(), newChatRoomId)) {
            log.info("이미 참여한 채팅방입니다.");
            return false;
        }

        ChatRoom chatroom = chatRoomRepository.findById(newChatRoomId).get();

        MemberChatRoomMapping memberChatRoomMapping = MemberChatRoomMapping.builder()
                .member(member)
                .chatRoom(chatroom)
                .build();

        memberChatRoomMapping = memberChatRoomMappingRepository.save(memberChatRoomMapping);

        return true;
    }

    private void updateLastCheckedAt(Member member, Long currentChatRoomId) {
        MemberChatRoomMapping memberChatRoomMapping = memberChatRoomMappingRepository.
                findByMemberIdAndChatRoomId(member.getId(), currentChatRoomId).get();

        memberChatRoomMapping.updateLastCheckedAt();
        memberChatRoomMappingRepository.save(memberChatRoomMapping);
    }

    /**
     * 채팅방 나가기
     */
    @Transactional
    public Boolean leaveChatRoom(Member member, Long chatRoomId) {
        if (!memberChatRoomMappingRepository.existsByMemberIdAndChatRoomId(member.getId(), chatRoomId)) {
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

        return memberChatRoomMappingList.stream().map(memberChatRoomMapping -> {
            ChatRoom chatRoom = memberChatRoomMapping.getChatRoom();
            chatRoom.setHasNewMessage(
                    messageRepository.existsByChatRoomIdAndCreatedAtAfter(chatRoom.getId(),
                            memberChatRoomMapping.getLastCheckedAt()));
            return chatRoom;
        }).toList();
    }


    /**
     * 메시지 저장
     */
    public Message saveMessage(Member member, Long chatRoomId, String text) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();

        Message message = Message.builder()
                .text(text)
                .member(member)
                .chatRoom(chatRoom)
                .createdAt(LocalDateTime.now())
                .build();
        return messageRepository.save(message);
    }

    /**
     * 메시지 가져오기
     */
    public List<Message> getMessageList(Long chatRoomId) {
        return messageRepository.findAllByChatRoomId(chatRoomId);
    }

    /**
     * 채팅방 조회
     */
    @Transactional(readOnly = true)
    public ChatRoomDto getChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
        return ChatRoomDto.from(chatRoom);
    }
}