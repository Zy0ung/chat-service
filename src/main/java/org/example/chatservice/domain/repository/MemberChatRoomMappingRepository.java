package org.example.chatservice.domain.repository;

import org.example.chatservice.domain.entity.MemberChatRoomMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiyoung
 */
@Repository
public interface MemberChatRoomMappingRepository extends JpaRepository<MemberChatRoomMapping, Long> {

    boolean existsByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);

    void deleteByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);

    List<MemberChatRoomMapping> findAllByMemberId(Long memberId);
}
