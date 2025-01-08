package org.example.chatservice.global.vos;

import lombok.AllArgsConstructor;
import org.example.chatservice.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author jiyoung
 */
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    Member member;
    Map<String, Object> attributeMap;

    public Member getMember() {
        return this.member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributeMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.member.getRole());
    }

    @Override
    public String getName() {
        return this.member.getName();
    }
}
