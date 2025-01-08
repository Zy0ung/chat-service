package org.example.chatservice.global.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.global.vos.CustomOAuth2User;
import org.example.chatservice.domain.emum.Gender;
import org.example.chatservice.domain.entity.Member;
import org.example.chatservice.domain.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

        Map<String, Object> attributeMap = oauth2User.getAttribute("kakao_account");
        String email = (String) attributeMap.get("email");

        Member member = memberRepository.findByEmail(email).orElseGet(() -> registerMember(attributeMap));
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private Member registerMember(Map<String, Object> attributeMap) {
        Member member = Member.builder()
                        .email((String) attributeMap.get("email"))
                        .nickname((String) ((Map) attributeMap.get("profile")).get("nickname"))
                        .name((String) attributeMap.get("name"))
                        .phoneNumber((String) attributeMap.get("phone_number"))
                        .gender(Gender.valueOf(((String) attributeMap.get("gender")).toUpperCase()))
                        .birthDay(getBirthDay(attributeMap))
                        .role("USER_ROLE")
                        .build();

        return memberRepository.save(member);
    }

    private LocalDate getBirthDay(Map<String, Object> attributeMap) {
        String birthYear = attributeMap.get("birthyear").toString();
        String birthDay = attributeMap.get("birthday").toString();

        return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
