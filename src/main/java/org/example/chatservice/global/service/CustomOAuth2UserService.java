package org.example.chatservice.global.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.domain.entity.Member;
import org.example.chatservice.domain.repository.MemberRepository;
import org.example.chatservice.global.vos.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * @author jiyoung
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = MemberFactory.create(oauth2User, userRequest);
                    return memberRepository.save(newMember);
                });
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }
}