package org.example.chatservice.global.vos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.example.chatservice.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author jiyoung
 */
public class CustomUserDetails extends CustomOAuth2User implements UserDetails {

    public CustomUserDetails(Member member, Map<String, Object> attributeMap) {
        super(member, attributeMap);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    @Override
    public String getPassword() {
        return this.member.getPassword();
    }

    @Override
    public String getUsername() {
        return this.member.getName();
    }
}
